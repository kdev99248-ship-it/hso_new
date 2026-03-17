package Game.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/** Quét các IP đang kết nối tới port 80/19129 bằng lệnh hệ điều hành (ss/netstat). */
public class PortHotspot {

    public static class Stat {
        public final String ip;
        public final int port;    // cổng local bị đánh (80 hoặc 19129)
        public final int count;   // số dòng kết nối thấy (xấp xỉ số kết nối)
        public Stat(String ip, int port, int count) {
            this.ip = ip; this.port = port; this.count = count;
        }
        @Override public String toString() {
            return ip + " | port " + port + " | conn=" + count;
        }
    }

    private static boolean isWindows() {
        String os = System.getProperty("os.name", "generic").toLowerCase(Locale.ROOT);
        return os.contains("win");
    }

    /** Quét 1 port, trả về top IP (giới hạn limit). */
    public static List<Stat> scanPort(int port, int limit) {
        Map<String,Integer> map = new HashMap<>();
        List<String> out = runScanCommand(port);
        for (String line : out) {
            String ip = parseRemoteIp(line, port);
            if (ip == null || ip.isEmpty() || "127.0.0.1".equals(ip)) continue;
            map.merge(ip, 1, Integer::sum);
        }
        List<Stat> list = new ArrayList<>();
        for (Map.Entry<String,Integer> e : map.entrySet()) {
            list.add(new Stat(e.getKey(), port, e.getValue()));
        }
        list.sort((a,b) -> Integer.compare(b.count, a.count));
        if (limit > 0 && list.size() > limit) {
            list = list.subList(0, limit);
        }
        return list;
    }

    /** Quét cả 2 cổng 80 và 19129. */
    public static List<Stat> scanBoth(int limitPerPort) {
        List<Stat> all = new ArrayList<>();
        all.addAll(scanPort(80, limitPerPort));
        all.addAll(scanPort(19129, limitPerPort));
        all.sort((a,b) -> Integer.compare(b.count, a.count));
        return all;
    }

    private static List<String> runScanCommand(int port) {
        List<String> out = new ArrayList<>();
        List<String> cmd;
        if (!isWindows()) {
            // Ưu tiên ss, fallback netstat
            if (cmdExists("ss")) {
                cmd = Arrays.asList("sh", "-lc",
                        "ss -Htn state established,syn-recv sport = :" + port + " || ss -Htn sport = :" + port);
            } else {
                cmd = Arrays.asList("sh", "-lc",
                        "netstat -nt 2>/dev/null | awk '$4 ~ /:" + port + "$/ {print}'");
            }
        } else {
            cmd = Arrays.asList("cmd.exe", "/c",
                    "netstat -ano -p tcp | findstr \":"
                            + port + " \"");
        }
        try {
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectErrorStream(true);
            Process p = pb.start();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) out.add(line.trim());
            }
            p.waitFor();
        } catch (IOException | InterruptedException ignored) {}
        return out;
    }

    private static boolean cmdExists(String name) {
        try {
            Process p = new ProcessBuilder("sh","-lc","command -v " + name).start();
            int code = p.waitFor();
            return code == 0;
        } catch (Exception e) { return false; }
    }

    /** Tách IP remote từ 1 dòng netstat/ss ứng với local port. */
    private static String parseRemoteIp(String line, int port) {
        try {
            if (!isWindows()) {
                // Ví dụ ss: "ESTAB ... 0.0.0.0:19129 203.0.113.5:54321"
                // Ví dụ netstat: "tcp 0 0 0.0.0.0:19129 203.0.113.5:54321 ESTABLISHED"
                String[] toks = line.split("\\s+");
                String remote = null;
                // lấy token chứa remote (thường là cột 5 với ss, cột 5 với netstat)
                for (int i = toks.length - 1; i >= 0; i--) {
                    if (toks[i].contains(":") && !toks[i].endsWith(":" + port)) {
                        remote = toks[i];
                        break;
                    }
                }
                if (remote == null) return null;
                // IPv6 [::ffff:1.2.3.4]:port hoặc 1.2.3.4:port
                remote = remote.replace("[", "").replace("]", "");
                int lastColon = remote.lastIndexOf(':');
                if (lastColon > 0) return remote.substring(0, lastColon);
                return remote;
            } else {
                // Windows: " TCP    0.0.0.0:19129   203.0.113.5:54321   SYN_RECEIVED   123"
                String[] toks = line.split("\\s+");
                String local = null, remote = null;
                for (String t : toks) {
                    if (t.contains(":")) {
                        if (local == null) local = t;
                        else { remote = t; break; }
                    }
                }
                if (remote == null) return null;
                int lastColon = remote.lastIndexOf(':');
                if (lastColon > 0) return remote.substring(0, lastColon);
                return remote;
            }
        } catch (Exception e) {
            return null;
        }
    }
}
