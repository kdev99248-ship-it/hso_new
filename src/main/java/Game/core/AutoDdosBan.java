package Game.core;

import java.util.*;
import java.util.concurrent.*;

/**
 * Tự động quét kết nối tới port 80 & 19129 và auto-ban IP vượt ngưỡng socket.
 * - scanOnceAndBan(...) => quét 1 lần và ban ngay nếu vượt ngưỡng
 * - enableAuto(...) / disableAuto() => bật quét định kỳ (mặc định mỗi 15s)
 */
public class AutoDdosBan {

    public static class Config {
        public volatile boolean enabled = false;
        public volatile int thresholdConnections = 10;     // >10 socket thì ban
        public volatile int banMinutes = 60;               // thời gian ban
        public volatile boolean firewall = true;           // có chặn firewall không
        public volatile boolean includeUdp19129 = false;   // có chặn UDP 19129 không
        public volatile boolean onlyWebAndGame = true;     // chỉ 80 & 19129 hay tất cả cổng
        public volatile int scanIntervalSec = 15;          // chu kỳ quét
        public final Set<String> whitelist = ConcurrentHashMap.newKeySet(); // IP không bao giờ auto-ban
    }

    private static final Config cfg = new Config();
    private static final ScheduledExecutorService sch = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "AutoDdosBan");
        t.setDaemon(true);
        return t;
    });
    private static ScheduledFuture<?> task;

    /** Cấu hình nhanh (có thể gọi lại nhiều lần). */
    public static synchronized void configure(Integer threshold, Integer banMinutes,
                                              Boolean firewall, Boolean includeUdp19129,
                                              Boolean onlyWebAndGame, Integer scanIntervalSec,
                                              Collection<String> whitelistIps) {
        if (threshold != null) cfg.thresholdConnections = Math.max(1, threshold);
        if (banMinutes != null) cfg.banMinutes = Math.max(1, banMinutes);
        if (firewall != null) cfg.firewall = firewall;
        if (includeUdp19129 != null) cfg.includeUdp19129 = includeUdp19129;
        if (onlyWebAndGame != null) cfg.onlyWebAndGame = onlyWebAndGame;
        if (scanIntervalSec != null) cfg.scanIntervalSec = Math.max(5, scanIntervalSec);
        if (whitelistIps != null) {
            cfg.whitelist.clear();
            for (String ip : whitelistIps) {
                String s = ip == null ? "" : ip.trim();
                if (!s.isEmpty()) cfg.whitelist.add(s);
            }
        }
        // nếu đang bật, restart lịch để áp dụng interval mới
        if (cfg.enabled) {
            disableAuto();
            enableAuto();
        }
    }

    /** Bật auto quét định kỳ. */
    public static synchronized void enableAuto() {
        if (cfg.enabled) return;
        cfg.enabled = true;
        task = sch.scheduleAtFixedRate(() -> {
            try {
                scanOnceAndBan(cfg.thresholdConnections, cfg.banMinutes, cfg.firewall,
                        cfg.includeUdp19129, cfg.onlyWebAndGame, cfg.whitelist);
            } catch (Throwable t) {
                System.out.println("[AutoDdosBan] tick error: " + t.getMessage());
            }
        }, 0, cfg.scanIntervalSec, TimeUnit.SECONDS);
        System.out.println("[AutoDdosBan] enabled; every " + cfg.scanIntervalSec + "s");
    }

    /** Tắt auto quét định kỳ. */
    public static synchronized void disableAuto() {
        cfg.enabled = false;
        if (task != null) {
            task.cancel(false);
            task = null;
        }
        System.out.println("[AutoDdosBan] disabled");
    }

    /** Quét 1 lần và auto-ban nếu IP có số socket > threshold. Trả về số IP đã ban. */
    public static int scanOnceAndBan(int threshold, int minutes, boolean firewall,
                                     boolean includeUdp19129, boolean onlyWebAndGame,
                                     Set<String> whitelist) {
        List<Game.core.PortHotspot.Stat> stats = Game.core.PortHotspot.scanBoth(200);
        int banned = 0;

        // Gom theo IP: nếu chỉ web+game thì tính cả 80 và 19129 chung
        Map<String, Integer> agg = new HashMap<>();
        for (Game.core.PortHotspot.Stat st : stats) {
            if (whitelist != null && whitelist.contains(st.ip)) continue;
            if (onlyWebAndGame && (st.port != 80 && st.port != 19129)) continue;
            agg.merge(st.ip, st.count, Integer::sum);
        }

        for (Map.Entry<String,Integer> e : agg.entrySet()) {
            String ip = e.getKey();
            int conn = e.getValue();
            if (conn > threshold) {
                try {
                    Game.core.DdosGuard.block(ip, minutes, firewall, includeUdp19129, onlyWebAndGame,
                            "auto-ban: conn=" + conn + " > " + threshold);
                    banned++;
                } catch (Throwable ex) {
                    System.out.println("[AutoDdosBan] block failed " + ip + ": " + ex.getMessage());
                }
            }
        }
        System.out.println("[AutoDdosBan] scanOnceAndBan: threshold=" + threshold + " -> banned=" + banned);
        return banned;
    }

    /** Truy cập cấu hình hiện tại (để UI hiển thị). */
    public static Config getConfig() { return cfg; }
}
