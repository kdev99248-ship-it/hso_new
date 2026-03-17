package Game.ai;

import Game.core.Manager;
import Game.core.ServerManager;
import Game.core.Util;
import Game.io.Session;
import Game.map.Map;
import Game.core.SQL;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class BotManager extends TelegramLongPollingBot {

    private final String botToken = "7886677034:AAHXRLqJ7pNuMkZexeMCQ5sxH_NLC888oJk";
    private final String chatId = "8398347756";

    @Override
    public String getBotUsername() {
        return "@Hiepsi_bot";
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    public void sendNotification(String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        try {
            execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendNotificationOther(String message, String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        try {
            execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processCommand(String command) throws IOException {
        if (command == null) {
            return;
        }
        command = command.trim();
        if (command.isEmpty()) {
            return;
        }

        if (command.startsWith("ban")) {
            String[] strs = command.split("\\s+");
            if (strs.length == 2) {
                Session.banAcc(strs[1]);
                sendNotification("Đã ban " + strs[1]);
            } else {
                sendNotification("Lỗi cú pháp");
            }
        } else if (command.equals("/memory")) {
            int num = 0;
            for (Map[] maps : Map.entrys) {
                for (Map m : maps) {
                    num += m.players.size();
                }
            }
            sendNotification("Bộ nhớ máy chủ hiện tại : " + ServerManager.getMemory() + "%\nSố người online " + num);
        } else if (command.equals("/accept")) {
            Manager.gI().isServerAdmin = !Manager.gI().isServerAdmin;
            sendNotification("Đã cho phép người chơi truy cập");
        } else if (command.startsWith("nap")) {
            String[] strs = command.split("\\s+");
            if (strs.length == 4) {
                topUp(strs[1], Integer.parseInt(strs[2]), Integer.parseInt(strs[3]));
            } else {
                sendNotification("Lỗi cú pháp");
            }
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update == null || !update.hasMessage()) {
            return;
        }
        var msg = update.getMessage();

        String command = msg.getText();
        if (command == null || command.isBlank()) {
            String cap = msg.getCaption();         
            if (cap != null && !cap.isBlank()) {
                command = cap;
            } else {
                return;                             
            }
        }

        Long fromId = (msg.getFrom() != null) ? msg.getFrom().getId() : null;
        if (fromId == null) {
            return;
        }

        if (fromId != Long.parseLong(this.chatId)) {
            sendNotificationOther("Bạn không phải admin của server", String.valueOf(fromId));
            return;
        }

        try {
            processCommand(command.trim());
        } catch (Exception e) {
            e.printStackTrace();
            sendNotificationOther("Có lỗi: " + e.getMessage(), String.valueOf(fromId));
        }
    }

    public void topUp(String name, int amount, int tigia) {
        try (Connection conn = SQL.gI().getConnection(); Statement statement = conn.createStatement()) {
            if (statement.executeUpdate(
                    "UPDATE `account` SET `coin` = `coin` +" + (amount * tigia) + ", `tiennap` = `tiennap` + " + amount + " WHERE `user` = '" + name + "';") > 0) {
                conn.commit();
                sendNotification("Đã nạp cho tài khoản " + name + " số coin " + Util.number_format(((long) amount * tigia)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            sendNotification("Lỗi nạp tài khoản " + name + " " + e.getMessage());
        }
    }
}
