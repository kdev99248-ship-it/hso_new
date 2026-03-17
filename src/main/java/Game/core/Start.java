package Game.core;

public class Start {

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                if (SQL.is_connected) {
                    Manager.gI().close();
                    SQL.gI().close();
                    System.out.println("SERVER STOPPED!");
                }
            }
        }));
        try {
            javax.swing.SwingUtilities.invokeLater(() -> new Game.admin.AdminPanel());
        } catch (Throwable ignored) {
        }
        ServerManager.gI().init();
        ServerManager.gI().running();
//        new java.util.Timer("HeadlessAutoBackup", true).scheduleAtFixedRate(
//                new java.util.TimerTask() {
//            private final java.util.concurrent.atomic.AtomicBoolean busy = new java.util.concurrent.atomic.AtomicBoolean(false);
//
//            @Override
//            public void run() {
//                if (!busy.compareAndSet(false, true)) {
//                    return;
//                }
//                new Thread(() -> {
//                    try {
//                        SaveData.process();
//                    } catch (Throwable ignored) {
//                    } finally {
//                        busy.set(false);
//                    }
//                }, "HeadlessBackup").start();
//            }
//        }, 60_000L, 60_000L);

    }
}
