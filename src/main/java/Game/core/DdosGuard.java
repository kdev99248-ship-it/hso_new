package Game.core;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Trung tâm quản lý block DDoS: app-level (CheckDDOS) + optional OS firewall.
 * Hỗ trợ TTL theo phút hoặc vĩnh viễn; tự cleanup khi hết hạn.
 */
public class DdosGuard {

    public static class Entry {
        public final String ip;
        public final long expiresAt; // epoch millis, <=0 = vĩnh viễn
        public final boolean firewall; // true nếu có block ở OS firewall
        public final boolean includeUdp19129;
        public final boolean onlyWebAndGame;
        public Entry(String ip, long expiresAt, boolean firewall, boolean includeUdp19129, boolean onlyWebAndGame) {
            this.ip = ip;
            this.expiresAt = expiresAt;
            this.firewall = firewall;
            this.includeUdp19129 = includeUdp19129;
            this.onlyWebAndGame = onlyWebAndGame;
        }
        public String expiryHuman() {
            if (expiresAt <= 0) return "vĩnh viễn";
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
            return fmt.format(Instant.ofEpochMilli(expiresAt));
        }
    }

    private static final Map<String, Entry> black = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService sch = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "DdosGuardCleanup");
        t.setDaemon(true);
        return t;
    });

    static {
        // dọn dẹp mỗi 30s
        sch.scheduleAtFixedRate(DdosGuard::cleanup, 30, 30, TimeUnit.SECONDS);
    }

    private static void cleanup() {
        long now = System.currentTimeMillis();
        List<String> toUnblock = new ArrayList<>();
        for (Map.Entry<String, Entry> e : black.entrySet()) {
            Entry val = e.getValue();
            if (val != null && val.expiresAt > 0 && now >= val.expiresAt) {
                toUnblock.add(e.getKey());
            }
        }
        for (String ip : toUnblock) {
            unblock(ip);
        }
    }

    /** Block IP (minutes=0 => vĩnh viễn). Có thể kèm firewall. */
    public static synchronized Entry block(String ip, int minutes, boolean firewall, boolean includeUdp19129, boolean onlyWebAndGame, String reason) {
        Objects.requireNonNull(ip, "ip");
        long expires = minutes > 0 ? System.currentTimeMillis() + minutes * 60_000L : -1L;

        // App-level
        try {
            CheckDDOS.blockIP(ip, "admin: " + (reason == null ? "" : reason));
        } catch (Throwable ignored) {}

        // OS-level firewall
        if (firewall) {
            try {
                DdosFirewall.Result r = DdosFirewall.blockIp(ip, includeUdp19129, onlyWebAndGame);
                System.out.println("[DdosGuard] firewall block " + ip + " => " + r.ok + " | " + r.output);
            } catch (Throwable ex) {
                System.out.println("[DdosGuard] firewall block failed: " + ex.getMessage());
            }
        }

        Entry e = new Entry(ip, expires, firewall, includeUdp19129, onlyWebAndGame);
        black.put(ip, e);
        // cắt bất kỳ session đang mở của IP
        try { CheckDDOS.DisconnectIP(ip); } catch (Throwable ignored) {}
        return e;
    }

    /** Gỡ block IP ở cả app-level và firewall (nếu có). */
    public static synchronized boolean unblock(String ip) {
        Entry old = black.remove(ip);
        try { CheckDDOS.unblockIP(ip); } catch (Throwable ignored) {}
        if (old != null && old.firewall) {
            try {
                DdosFirewall.Result r = DdosFirewall.unblockIp(ip);
                System.out.println("[DdosGuard] firewall unblock " + ip + " => " + r.ok + " | " + r.output);
            } catch (Throwable ex) {
                System.out.println("[DdosGuard] firewall unblock failed: " + ex.getMessage());
            }
        }
        return old != null;
    }

    /** Ảnh chụp hiện thời của các entry có TTL/ghi nhận trong guard. */
    public static List<Entry> snapshot() {
        List<Entry> out = new ArrayList<>(black.values());
        out.sort((a,b) -> a.ip.compareTo(b.ip));
        return Collections.unmodifiableList(out);
    }

    /** Ảnh chụp mức app-level (CheckDDOS) để hiển thị thêm trong UI. */
    public static Set<String> appLevelBlockedSnapshot() {
        try {
            return CheckDDOS.getBlockedIPsSnapshot();
        } catch (Throwable t) {
            return Collections.emptySet();
        }
    }
}
