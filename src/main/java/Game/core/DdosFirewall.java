package Game.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * OS-level firewall helper: Windows (netsh advfirewall) và Linux (iptables) fallback.
 * Mục tiêu: block/unblock IP cho các cổng 80 (HTTP) và 19129 (game).
 */
public class DdosFirewall {

    public static class Result {
        public final boolean ok;
        public final String output;
        public Result(boolean ok, String output) { this.ok = ok; this.output = output; }
    }

    private static boolean isWindows() {
        String os = System.getProperty("os.name", "generic").toLowerCase();
        return os.contains("win");
    }

    private static Result run(List<String> cmd) {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        StringBuilder out = new StringBuilder();
        try {
            Process p = pb.start();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) out.append(line).append("\n");
            }
            int code = p.waitFor();
            return new Result(code == 0, out.toString());
        } catch (IOException | InterruptedException e) {
            return new Result(false, "ERR: " + e.getMessage());
        }
    }

    private static String ruleName(String ip, String tag) {
        return "DDOS_BLOCK_" + ip.replace(":", "_") + "_" + tag;
    }

    /** Block IP ở firewall: chỉ 80/19129 hoặc tất cả cổng; có thể kèm UDP 19129 */
    public static Result blockIp(String ip, boolean includeUDP19129, boolean onlyWebAndGame) {
        if (isWindows()) {
            List<Result> results = new ArrayList<>();
            if (onlyWebAndGame) {
                // TCP 80 + TCP 19129 (+ UDP 19129 optional)
                results.add(run(List.of("netsh", "advfirewall", "firewall", "add", "rule",
                        "name=" + ruleName(ip, "HTTP80_TCP"), "dir=in", "action=block", "remoteip=" + ip,
                        "protocol=TCP", "localport=80")));
                results.add(run(List.of("netsh", "advfirewall", "firewall", "add", "rule",
                        "name=" + ruleName(ip, "GAME19129_TCP"), "dir=in", "action=block", "remoteip=" + ip,
                        "protocol=TCP", "localport=19129")));
                if (includeUDP19129) {
                    results.add(run(List.of("netsh", "advfirewall", "firewall", "add", "rule",
                            "name=" + ruleName(ip, "GAME19129_UDP"), "dir=in", "action=block", "remoteip=" + ip,
                            "protocol=UDP", "localport=19129")));
                }
            } else {
                results.add(run(List.of("netsh", "advfirewall", "firewall", "add", "rule",
                        "name=" + ruleName(ip, "ALL_TCP"), "dir=in", "action=block", "remoteip=" + ip,
                        "protocol=TCP", "localport=any")));
                if (includeUDP19129) {
                    results.add(run(List.of("netsh", "advfirewall", "firewall", "add", "rule",
                            "name=" + ruleName(ip, "ALL_UDP"), "dir=in", "action=block", "remoteip=" + ip,
                            "protocol=UDP", "localport=any")));
                }
            }
            boolean ok = results.stream().allMatch(r -> r.ok);
            StringBuilder sb = new StringBuilder();
            for (Result r : results) sb.append(r.output);
            return new Result(ok, sb.toString());
        } else {
            // Linux iptables fallback
            List<Result> results = new ArrayList<>();
            if (onlyWebAndGame) {
                results.add(run(List.of("iptables", "-I", "INPUT", "-p", "tcp", "--dport", "80", "-s", ip, "-j", "DROP")));
                results.add(run(List.of("iptables", "-I", "INPUT", "-p", "tcp", "--dport", "19129", "-s", ip, "-j", "DROP")));
                if (includeUDP19129) {
                    results.add(run(List.of("iptables", "-I", "INPUT", "-p", "udp", "--dport", "19129", "-s", ip, "-j", "DROP")));
                }
            } else {
                results.add(run(List.of("iptables", "-I", "INPUT", "-s", ip, "-j", "DROP")));
            }
            boolean ok = results.stream().allMatch(r -> r.ok);
            StringBuilder sb = new StringBuilder();
            for (Result r : results) sb.append(r.output);
            return new Result(ok, sb.toString());
        }
    }

    /** Gỡ block IP ở firewall (best-effort) */
    public static Result unblockIp(String ip) {
        if (isWindows()) {
            List<Result> results = new ArrayList<>();
            String[] tags = {"HTTP80_TCP", "GAME19129_TCP", "GAME19129_UDP", "ALL_TCP", "ALL_UDP"};
            for (String tag : tags) {
                results.add(run(List.of("netsh", "advfirewall", "firewall", "delete", "rule",
                        "name=" + ruleName(ip, tag))));
            }
            boolean ok = results.stream().allMatch(r -> r.ok || r.output.toLowerCase().contains("no rules match"));
            StringBuilder sb = new StringBuilder();
            for (Result r : results) sb.append(r.output);
            return new Result(ok, sb.toString());
        } else {
            List<Result> results = new ArrayList<>();
            results.add(run(List.of("iptables", "-D", "INPUT", "-p", "tcp", "--dport", "80", "-s", ip, "-j", "DROP")));
            results.add(run(List.of("iptables", "-D", "INPUT", "-p", "tcp", "--dport", "19129", "-s", ip, "-j", "DROP")));
            results.add(run(List.of("iptables", "-D", "INPUT", "-p", "udp", "--dport", "19129", "-s", ip, "-j", "DROP")));
            results.add(run(List.of("iptables", "-D", "INPUT", "-s", ip, "-j", "DROP")));
            boolean ok = results.stream().anyMatch(r -> r.ok);
            StringBuilder sb = new StringBuilder();
            for (Result r : results) sb.append(r.output);
            return new Result(ok, sb.toString());
        }
    }
}
