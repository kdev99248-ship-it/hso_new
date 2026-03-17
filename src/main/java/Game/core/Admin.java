package Game.core;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Game.Helps.ItemStar;
import Game.Helps.Medal;
import Game.client.Player;
import Game.io.Message;
import Game.io.Session;
import Game.template.Item3;
import Game.template.Item47;
import Game.template.ItemTemplate3;
import Game.template.ItemTemplate4;
import Game.template.ItemTemplate7;
import Game.template.MaterialMedal;
import Game.template.Option;

public class Admin {
    public static HashMap<String, Integer> topLevel = new HashMap<>();
    public static HashMap<String, Integer> topEvent = new HashMap<>();

    public static void randomMedal(Player p, byte color_, byte tier_, boolean isLock) {
        ItemTemplate3 temp = ItemTemplate3.item.get(Util.random(4587, 4591));
        Item3 itbag = new Item3();
        itbag.id = temp.getId();
        itbag.clazz = temp.getClazz();
        itbag.type = temp.getType();
        itbag.level = 1; // level required
        itbag.icon = temp.getIcon();
        itbag.color = color_;
        itbag.part = temp.getPart();
        itbag.islock = isLock;
        itbag.name = temp.getName();
        itbag.tier = 0;
        //
        List<Option> opnew = new ArrayList<>();
        byte typest = (byte) Util.random(0, 5);
        int _st;
        byte dongan;
        if (color_ == 0) {
            _st = Util.random(130, 140);
            dongan = (byte) Util.random(5, 7);
        } else if (color_ == 1) {
            _st = Util.random(130, 150);
            dongan = (byte) Util.random(5, 8);
        } else if (color_ == 2) {
            _st = Util.random(140, 160);
            dongan = (byte) Util.random(5, 8);
        } else if (color_ == 3) {
            _st = Util.random(150, 170);
            dongan = (byte) Util.random(5, 9);
        } else {
            _st = Util.random(160, 180);
            dongan = (byte) Util.random(7, 11);
        }
        // thêm dòng st gốc
        opnew.add(new Option(typest, _st, itbag.id));
        opnew.add(new Option(96, dongan, itbag.id));
        //
        itbag.op = new ArrayList<>();
        itbag.opMedal = Medal.CreateMedal(dongan, color_, itbag.id);
        itbag.op.addAll(opnew);
        itbag.time_use = 0;
        itbag.item_medal = new short[5];

        int material_type_1st = Util.random(0, 7);
        int material_type_2nd = Util.random(0, 7);
        while (material_type_1st == material_type_2nd) {
            material_type_2nd = Util.random(0, 7);
        }
        itbag.item_medal[0] = (short) (MaterialMedal.m_white[material_type_1st][Util.random(0, 10)] + 200);
        itbag.item_medal[1] = (short) (MaterialMedal.m_white[material_type_2nd][Util.random(0, 10)] + 200);
        itbag.item_medal[2] = (short) (MaterialMedal.m_blue[Util.random(0, 10)] + 200);
        itbag.item_medal[3] = (short) (MaterialMedal.m_yellow[Util.random(0, 10)] + 200);
        itbag.item_medal[4] = (short) (MaterialMedal.m_violet[Util.random(0, 10)] + 200);

        for (byte i = 0; i < tier_; i++) {
            itbag.tier = (byte) (i + 1);
            Medal.UpgradeMedal(itbag);
        }
        p.item.add_item_inventory3(itbag);
    }

    public static void randomTT(Session conn, byte color, byte type) throws IOException {
        short type_item = ItemStar.ConvertType(type, conn.p.clazz);
        short id_item = ItemStar.GetIDItem(type, conn.p.clazz);
        List<Option> ops = ItemStar.GetOpsItemStar(conn.p.clazz, (byte) type_item, 0);

        Item3 itbag = new Item3();
        itbag.id = id_item;
        itbag.name = ItemTemplate3.item.get(id_item).getName();
        itbag.clazz = ItemTemplate3.item.get(id_item).getClazz();
        itbag.type = ItemTemplate3.item.get(id_item).getType();
        itbag.level = 45;
        itbag.icon = ItemTemplate3.item.get(id_item).getIcon();
        itbag.op = new ArrayList<>();
        for (Option o : ops) {
            int pr = o.getParam(0);
            int pr1 = (int) (pr * color * 0.25);
            if ((o.id >= 58 && o.id <= 60) || (o.id >= 100 && o.id <= 107))
                itbag.op.add(new Option(o.id, pr, itbag.id));
            else if (o.id == 37 || o.id == 38) {
                itbag.op.add(new Option(o.id, 1, itbag.id));
            } else
                itbag.op.add(new Option(o.id, pr1, itbag.id));
        }
        int[] opAo = { -111, -110, -109, -108, -107 };
        int[] opNon = { -102, -113, -105 };
        int[] opVK = { -101, -113, -86, -84, -82, -80 };
        int[] opNhan = { -89, -87, -104, -86, -84, -82, -80 };
        int[] opDayChuyen = { -87, -105, -103, -91 };
        int[] opGang = { -89, -103, -91 };
        int[] opGiay = { -104, -103, -91 };

        if (color == 4) {
            if (itbag.type == 0 || itbag.type == 1) {
                int percent = Util.nextInt(0, 100);
                if (percent > 85) {
                    int opid1 = opAo[Util.nextInt(opAo.length)];
                    int opid2 = opAo[Util.nextInt(opAo.length)];
                    while (opid1 == opid2) {
                        opid1 = opAo[Util.nextInt(opAo.length)];
                    }
                    itbag.op.add(new Option(opid1, Util.random(100, 200), itbag.id));
                    itbag.op.add(new Option(opid2, Util.random(100, 200), itbag.id));
                } else {
                    int opid = opAo[Util.nextInt(opAo.length)];
                    itbag.op.add(new Option(opid, Util.random(100, 200), itbag.id));
                }
            } else if (itbag.type == 2) {
                int percent = Util.nextInt(0, 100);
                if (percent > 85) {
                    int opid1 = opNon[Util.nextInt(opNon.length)];
                    int opid2 = opNon[Util.nextInt(opNon.length)];
                    while (opid1 == opid2) {
                        opid1 = opNon[Util.nextInt(opNon.length)];
                    }
                    itbag.op.add(new Option(opid1, Util.random(100, 200), itbag.id));
                    itbag.op.add(new Option(opid2, Util.random(100, 200), itbag.id));
                } else {
                    int opid = opNon[Util.nextInt(opNon.length)];
                    itbag.op.add(new Option(opid, Util.random(100, 200), itbag.id));
                }
            } else if (itbag.type == 3) {
                int percent = Util.nextInt(0, 100);
                if (percent > 85) {
                    int opid1 = opGang[Util.nextInt(opGang.length)];
                    int opid2 = opGang[Util.nextInt(opGang.length)];
                    while (opid1 == opid2) {
                        opid1 = opGang[Util.nextInt(opGang.length)];
                    }
                    itbag.op.add(new Option(opid1, Util.random(100, 200), itbag.id));
                    itbag.op.add(new Option(opid2, Util.random(100, 200), itbag.id));
                } else {
                    int opid = opGang[Util.nextInt(opGang.length)];
                    itbag.op.add(new Option(opid, Util.random(100, 200), itbag.id));
                }
            } else if (itbag.type == 4) {
                int percent = Util.nextInt(0, 100);
                if (percent > 85) {
                    int opid1 = opNhan[Util.nextInt(opNhan.length)];
                    int opid2 = opNhan[Util.nextInt(opNhan.length)];
                    while (opid1 == opid2) {
                        opid1 = opNhan[Util.nextInt(opNhan.length)];
                    }
                    itbag.op.add(new Option(opid1, Util.random(100, 200), itbag.id));
                    itbag.op.add(new Option(opid2, Util.random(100, 200), itbag.id));
                } else {
                    int opid = opNhan[Util.nextInt(opNhan.length)];
                    itbag.op.add(new Option(opid, Util.random(100, 200), itbag.id));
                }
            } else if (itbag.type == 5) {
                int percent = Util.nextInt(0, 100);
                if (percent > 85) {
                    int opid1 = opDayChuyen[Util.nextInt(opDayChuyen.length)];
                    int opid2 = opDayChuyen[Util.nextInt(opDayChuyen.length)];
                    while (opid1 == opid2) {
                        opid1 = opDayChuyen[Util.nextInt(opDayChuyen.length)];
                    }
                    itbag.op.add(new Option(opid1, Util.random(100, 200), itbag.id));
                    itbag.op.add(new Option(opid2, Util.random(100, 200), itbag.id));
                } else {
                    int opid = opDayChuyen[Util.nextInt(opDayChuyen.length)];
                    itbag.op.add(new Option(opid, Util.random(100, 200), itbag.id));
                }
            } else if (itbag.type == 6) {
                int percent = Util.nextInt(0, 100);
                if (percent > 85) {
                    int opid1 = opGiay[Util.nextInt(opGiay.length)];
                    int opid2 = opGiay[Util.nextInt(opGiay.length)];
                    while (opid1 == opid2) {
                        opid1 = opGiay[Util.nextInt(opGiay.length)];
                    }
                    itbag.op.add(new Option(opid1, Util.random(100, 200), itbag.id));
                    itbag.op.add(new Option(opid2, Util.random(100, 200), itbag.id));
                } else {
                    int opid = opGiay[Util.nextInt(opGiay.length)];
                    itbag.op.add(new Option(opid, Util.random(100, 200), itbag.id));
                }
            } else if (itbag.type > 6) {
                int percent = Util.nextInt(0, 100);
                if (percent > 85) {
                    int opid1 = opVK[Util.nextInt(opVK.length)];
                    int opid2 = opVK[Util.nextInt(opVK.length)];
                    while (opid1 == opid2) {
                        opid1 = opVK[Util.nextInt(opVK.length)];
                    }
                    itbag.op.add(new Option(opid1, Util.random(100, 200), itbag.id));
                    itbag.op.add(new Option(opid2, Util.random(100, 200), itbag.id));
                } else {
                    int opid = opVK[Util.nextInt(opVK.length)];
                    itbag.op.add(new Option(opid, Util.random(100, 200), itbag.id));
                }
            }
        } else if (color == 5) {
            if (itbag.type == 0 || itbag.type == 1) {
                int percent = Util.nextInt(0, 100);
                if (percent > 85) {
                    int opid1 = opAo[Util.nextInt(opAo.length)];
                    int opid2 = opAo[Util.nextInt(opAo.length)];
                    int opid3 = opAo[Util.nextInt(opAo.length)];
                    while ((opid1 == opid2) || (opid1 == opid3)) {
                        opid1 = opAo[Util.nextInt(opAo.length)];
                    }
                    while ((opid2 == opid1) || (opid2 == opid3)) {
                        opid2 = opAo[Util.nextInt(opAo.length)];
                    }
                    while ((opid3 == opid2) || (opid1 == opid3)) {
                        opid3 = opAo[Util.nextInt(opAo.length)];
                    }
                    if (percent > 95) {
                        itbag.op.add(new Option(opid3, Util.random(100, 200), itbag.id));
                    }
                    itbag.op.add(new Option(opid1, Util.random(100, 200), itbag.id));
                    itbag.op.add(new Option(opid2, Util.random(100, 200), itbag.id));
                } else {
                    int opid = opAo[Util.nextInt(opAo.length)];
                    itbag.op.add(new Option(opid, Util.random(100, 200), itbag.id));
                }
            } else if (itbag.type == 2) {
                int percent = Util.nextInt(0, 100);
                if (percent > 85) {
                    int opid1 = opNon[Util.nextInt(opNon.length)];
                    int opid2 = opNon[Util.nextInt(opNon.length)];
                    int opid3 = opNon[Util.nextInt(opNon.length)];
                    while ((opid1 == opid2) || (opid1 == opid3)) {
                        opid1 = opNon[Util.nextInt(opNon.length)];
                    }
                    while ((opid2 == opid1) || (opid2 == opid3)) {
                        opid2 = opNon[Util.nextInt(opNon.length)];
                    }
                    while ((opid3 == opid2) || (opid1 == opid3)) {
                        opid3 = opNon[Util.nextInt(opNon.length)];
                    }
                    if (percent > 95) {
                        itbag.op.add(new Option(opid3, Util.random(100, 200), itbag.id));
                    }
                    itbag.op.add(new Option(opid1, Util.random(100, 200), itbag.id));
                    itbag.op.add(new Option(opid2, Util.random(100, 200), itbag.id));
                } else {
                    int opid = opNon[Util.nextInt(opNon.length)];
                    itbag.op.add(new Option(opid, Util.random(100, 200), itbag.id));
                }
            } else if (itbag.type == 3) {
                int percent = Util.nextInt(0, 100);
                if (percent > 85) {
                    int opid1 = opGang[Util.nextInt(opGang.length)];
                    int opid2 = opGang[Util.nextInt(opGang.length)];
                    int opid3 = opGang[Util.nextInt(opGang.length)];
                    while ((opid1 == opid2) || (opid1 == opid3)) {
                        opid1 = opGang[Util.nextInt(opGang.length)];
                    }
                    while ((opid2 == opid1) || (opid2 == opid3)) {
                        opid2 = opGang[Util.nextInt(opGang.length)];
                    }
                    while ((opid3 == opid2) || (opid1 == opid3)) {
                        opid3 = opGang[Util.nextInt(opGang.length)];
                    }
                    if (percent > 95) {
                        itbag.op.add(new Option(opid3, Util.random(100, 200), itbag.id));
                    }
                } else {
                    int opid = opGang[Util.nextInt(opGang.length)];
                    itbag.op.add(new Option(opid, Util.random(100, 200), itbag.id));
                }
            } else if (itbag.type == 4) {
                int percent = Util.nextInt(0, 100);
                if (percent > 85) {
                    int opid1 = opNhan[Util.nextInt(opNhan.length)];
                    int opid2 = opNhan[Util.nextInt(opNhan.length)];
                    int opid3 = opNhan[Util.nextInt(opNhan.length)];
                    while ((opid1 == opid2) || (opid1 == opid3)) {
                        opid1 = opNhan[Util.nextInt(opNhan.length)];
                    }
                    while ((opid2 == opid1) || (opid2 == opid3)) {
                        opid2 = opNhan[Util.nextInt(opNhan.length)];
                    }
                    while ((opid3 == opid2) || (opid1 == opid3)) {
                        opid3 = opNhan[Util.nextInt(opNhan.length)];
                    }
                    if (percent > 95) {
                        itbag.op.add(new Option(opid3, Util.random(100, 200), itbag.id));
                    }
                    itbag.op.add(new Option(opid1, Util.random(100, 200), itbag.id));
                    itbag.op.add(new Option(opid2, Util.random(100, 200), itbag.id));
                } else {
                    int opid = opNhan[Util.nextInt(opNhan.length)];
                    itbag.op.add(new Option(opid, Util.random(100, 200), itbag.id));
                }
            } else if (itbag.type == 5) {
                int percent = Util.nextInt(0, 100);
                if (percent > 85) {
                    int opid1 = opDayChuyen[Util.nextInt(opDayChuyen.length)];
                    int opid2 = opDayChuyen[Util.nextInt(opDayChuyen.length)];
                    int opid3 = opDayChuyen[Util.nextInt(opDayChuyen.length)];
                    while ((opid1 == opid2) || (opid1 == opid3)) {
                        opid1 = opDayChuyen[Util.nextInt(opDayChuyen.length)];
                    }
                    while ((opid2 == opid1) || (opid2 == opid3)) {
                        opid2 = opDayChuyen[Util.nextInt(opDayChuyen.length)];
                    }
                    while ((opid3 == opid2) || (opid1 == opid3)) {
                        opid3 = opDayChuyen[Util.nextInt(opDayChuyen.length)];
                    }
                    if (percent > 95) {
                        itbag.op.add(new Option(opid3, Util.random(100, 200), itbag.id));
                    }
                    itbag.op.add(new Option(opid1, Util.random(100, 200), itbag.id));
                    itbag.op.add(new Option(opid2, Util.random(100, 200), itbag.id));
                } else {
                    int opid = opDayChuyen[Util.nextInt(opDayChuyen.length)];
                    itbag.op.add(new Option(opid, Util.random(100, 200), itbag.id));
                }
            } else if (itbag.type == 6) {
                int percent = Util.nextInt(0, 100);
                if (percent > 85) {
                    int opid1 = opGiay[Util.nextInt(opGiay.length)];
                    int opid2 = opGiay[Util.nextInt(opGiay.length)];
                    int opid3 = opGiay[Util.nextInt(opGiay.length)];
                    while ((opid1 == opid2) || (opid1 == opid3)) {
                        opid1 = opGiay[Util.nextInt(opGiay.length)];
                    }
                    while ((opid2 == opid1) || (opid2 == opid3)) {
                        opid2 = opGiay[Util.nextInt(opGiay.length)];
                    }
                    while ((opid3 == opid2) || (opid1 == opid3)) {
                        opid3 = opGiay[Util.nextInt(opGiay.length)];
                    }
                    if (percent > 95) {
                        itbag.op.add(new Option(opid3, Util.random(100, 200), itbag.id));
                    }
                    itbag.op.add(new Option(opid1, Util.random(100, 200), itbag.id));
                    itbag.op.add(new Option(opid2, Util.random(100, 200), itbag.id));
                } else {
                    int opid = opGiay[Util.nextInt(opGiay.length)];
                    itbag.op.add(new Option(opid, Util.random(100, 200), itbag.id));
                }
            } else if (itbag.type > 7) {
                int percent = Util.nextInt(0, 100);
                if (percent > 90) {
                    int opid1 = opVK[Util.nextInt(opVK.length)];
                    int opid2 = opVK[Util.nextInt(opVK.length)];
                    int opid3 = opVK[Util.nextInt(opVK.length)];
                    while ((opid1 == opid2) || (opid1 == opid3)) {
                        opid1 = opVK[Util.nextInt(opVK.length)];
                    }
                    while ((opid2 == opid1) || (opid2 == opid3)) {
                        opid2 = opVK[Util.nextInt(opVK.length)];
                    }
                    while ((opid3 == opid2) || (opid1 == opid3)) {
                        opid3 = opVK[Util.nextInt(opVK.length)];
                    }
                    if (percent > 95) {
                        itbag.op.add(new Option(opid3, Util.random(100, 200), itbag.id));
                    }
                    itbag.op.add(new Option(opid1, Util.random(100, 200), itbag.id));
                    itbag.op.add(new Option(opid2, Util.random(100, 200), itbag.id));
                } else {
                    int opid = opVK[Util.nextInt(opVK.length)];
                    itbag.op.add(new Option(opid, Util.random(100, 200), itbag.id));
                }
            }
        }
        itbag.color = color;
        itbag.part = ItemTemplate3.item.get(id_item).getPart();
        itbag.tier = 0;
        itbag.time_use = 0;
        itbag.islock = true;
        conn.p.item.add_item_inventory3(itbag);
        conn.p.item.char_inventory(3);
    }

    public static void setTop() {
        topEvent();
    }

    public static void topEvent() {
        topEvent.put("", 1);
    }

    public static void setThanhTich() {
        for (Map.Entry<String, Integer> entry : Manager.gI().thanh_tich.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            if (value == 0) {
                Manager.gI().ty_phu.add(key);
            } else if (value == 1) {
                Manager.gI().trieu_phu.add(key);
            } else {
                Manager.gI().dai_gia.add(key);
            }
        }
    }

    public static void quatopLevel(Session conn) throws IOException {
        if (!Admin.topLevel.containsKey(conn.p.name)) {
            Service.send_notice_box(conn, "Không có tên");
            return;
        }
        if (conn.p.item.get_inventory_able() < 5) {
            Service.send_notice_box(conn, "Hành trang cần tối thiểu 5 ô trống");
            return;
        }
        switch (Admin.topLevel.get(conn.p.name)) {
            case 1: {
                conn.p.update_ngoc(70000, "Nhận %s ngọc từ quà top Level");
                conn.p.update_vang(100000000L, "Nhận %s vàng từ quà top Level");

                Item47 item47 = new Item47();
                item47.id = 14;
                item47.quantity = 100;
                conn.p.item.add_item_inventory47(7, item47);

                item47.id = 471;
                item47.quantity = 1;
                conn.p.item.add_item_inventory47(7, item47);

                ItemTemplate3 temp3 = ItemTemplate3.item.get(4580);
                Item3 it = new Item3();
                it.id = temp3.getId();
                it.name = temp3.getName();
                it.clazz = temp3.getClazz();
                it.type = temp3.getType();
                it.level = temp3.getLevel();
                it.icon = temp3.getIcon();
                it.op = temp3.getOp();
                it.color = 5;
                it.part = temp3.getPart();
                conn.p.item.add_item_inventory3(it);

                temp3 = ItemTemplate3.item.get(4706);// Tóc
                it = new Item3();
                it.id = temp3.getId();
                it.name = temp3.getName();
                it.clazz = temp3.getClazz();
                it.type = temp3.getType();
                it.level = temp3.getLevel();
                it.icon = temp3.getIcon();
                it.op = temp3.getOp();
                it.color = 5;
                it.part = temp3.getPart();
                conn.p.item.add_item_inventory3(it);

                Admin.randomMedal(conn.p, (byte) 4, (byte) 10, false);
                Service.send_notice_box(conn, "Bạn đã nhận được quà");
                Admin.topLevel.remove(conn.p.name);
                break;
            }
            case 2: {
                conn.p.update_ngoc(40000, "Nhận %s ngọc từ quà top Level");
                conn.p.update_vang(100000000L, "Nhận %s vàng từ quà top Level");

                Item47 item47 = new Item47();
                item47.id = 14;
                item47.quantity = 50;
                conn.p.item.add_item_inventory47(7, item47);

                item47.id = 471;
                item47.quantity = 1;
                conn.p.item.add_item_inventory47(7, item47);

                ItemTemplate3 temp3 = ItemTemplate3.item.get(4580);
                Item3 it = new Item3();
                it.id = temp3.getId();
                it.name = temp3.getName();
                it.clazz = temp3.getClazz();
                it.type = temp3.getType();
                it.level = temp3.getLevel();
                it.icon = temp3.getIcon();
                it.op = temp3.getOp();
                it.color = 5;
                it.part = temp3.getPart();
                conn.p.item.add_item_inventory3(it);

                temp3 = ItemTemplate3.item.get(4706);// Tóc
                it = new Item3();
                it.id = temp3.getId();
                it.name = temp3.getName();
                it.clazz = temp3.getClazz();
                it.type = temp3.getType();
                it.level = temp3.getLevel();
                it.icon = temp3.getIcon();
                it.op = temp3.getOp();
                it.color = 5;
                it.part = temp3.getPart();
                conn.p.item.add_item_inventory3(it);

                Admin.randomMedal(conn.p, (byte) 4, (byte) 6, false);
                Service.send_notice_box(conn, "Bạn đã nhận được quà");
                Admin.topLevel.remove(conn.p.name);
                break;
            }
            case 3: {
                conn.p.update_ngoc(20000, "Nhận %s ngọc từ quà top Level");
                conn.p.update_vang(80000000L, "Nhận %s vàng từ quà top Level");

                Item47 item47 = new Item47();
                item47.id = 14;
                item47.quantity = 40;
                conn.p.item.add_item_inventory47(7, item47);

                item47.id = 471;
                item47.quantity = 1;
                conn.p.item.add_item_inventory47(7, item47);

                ItemTemplate3 temp3 = ItemTemplate3.item.get(4584);
                Item3 it = new Item3();
                it.id = temp3.getId();
                it.name = temp3.getName();
                it.clazz = temp3.getClazz();
                it.type = temp3.getType();
                it.level = temp3.getLevel();
                it.icon = temp3.getIcon();
                it.op = temp3.getOp();
                it.color = 5;
                it.part = temp3.getPart();
                conn.p.item.add_item_inventory3(it);

                temp3 = ItemTemplate3.item.get(4706);// Tóc
                it = new Item3();
                it.id = temp3.getId();
                it.name = temp3.getName();
                it.clazz = temp3.getClazz();
                it.type = temp3.getType();
                it.level = temp3.getLevel();
                it.icon = temp3.getIcon();
                it.op = temp3.getOp();
                it.color = 5;
                it.part = temp3.getPart();
                conn.p.item.add_item_inventory3(it);

                Admin.randomMedal(conn.p, (byte) 4, (byte) 6, true);
                Service.send_notice_box(conn, "Bạn đã nhận được quà");
                Admin.topLevel.remove(conn.p.name);
                break;
            }
            case 4: {
                Item47 item47 = new Item47();
                item47.id = 14;
                item47.quantity = 30;
                conn.p.item.add_item_inventory47(7, item47);

                item47.id = 471;
                item47.quantity = 1;
                conn.p.item.add_item_inventory47(7, item47);

                ItemTemplate3 temp3 = ItemTemplate3.item.get(Util.random(4577, 4585));
                Item3 it = new Item3();
                it.id = temp3.getId();
                it.name = temp3.getName();
                it.clazz = temp3.getClazz();
                it.type = temp3.getType();
                it.level = temp3.getLevel();
                it.icon = temp3.getIcon();
                it.op = temp3.getOp();
                it.color = 5;
                it.part = temp3.getPart();
                conn.p.item.add_item_inventory3(it);

                Admin.randomMedal(conn.p, (byte) 4, (byte) 6, true);
                Service.send_notice_box(conn, "Bạn đã nhận được quà");
                Admin.topLevel.remove(conn.p.name);
                break;
            }
        }
        Manager.gI().notifierBot.sendNotification(conn.p.name + " đã nhận quà top level");
    }

    public static void quatopEvent(Session conn) throws IOException {
        if (!Admin.topEvent.containsKey(conn.p.name)) {
            Service.send_notice_box(conn, "Không có tên");
            return;
        }
        if (conn.p.item.get_inventory_able() < 10) {
            Service.send_notice_box(conn, "Hành trang cần tối thiểu 10 ô trống");
            return;
        }
        switch (Admin.topEvent.get(conn.p.name)) {
            case 1: {
                ItemTemplate3 temp3 = ItemTemplate3.item.get(4640); // cánh
                Item3 it = new Item3();
                it.id = temp3.getId();
                it.name = temp3.getName();
                it.clazz = temp3.getClazz();
                it.type = temp3.getType();
                it.level = temp3.getLevel();
                it.icon = temp3.getIcon();
                it.op = temp3.getOp();
                it.color = 5;
                it.part = temp3.getPart();
                conn.p.item.add_item_inventory3(it);

                int id = 4812;
                if (conn.p.clazz <= 1) {
                    id = 4813;
                }
                temp3 = ItemTemplate3.item.get(id);// Thời trang rồng
                it = new Item3();
                it.id = temp3.getId();
                it.name = temp3.getName();
                it.clazz = temp3.getClazz();
                it.type = temp3.getType();
                it.level = temp3.getLevel();
                it.icon = temp3.getIcon();
                it.op = new ArrayList<>();
                it.op.add(new Option(7, 7000));
                it.op.add(new Option(8, 7000));
                it.op.add(new Option(9, 7000));
                it.op.add(new Option(10, 7000));
                it.op.add(new Option(11, 7000));
                it.op.add(new Option(27, 1500));
                it.op.add(new Option(-128, 500));
                it.color = 5;
                it.part = temp3.getPart();
                conn.p.item.add_item_inventory3(it);

                temp3 = ItemTemplate3.item.get(4617);// trứng rồng lửa
                it = new Item3();
                it.id = temp3.getId();
                it.name = temp3.getName();
                it.clazz = temp3.getClazz();
                it.type = temp3.getType();
                it.level = temp3.getLevel();
                it.icon = temp3.getIcon();
                it.op = temp3.getOp();
                it.color = 5;
                it.part = temp3.getPart();
                conn.p.item.add_item_inventory3(it);

                Admin.randomMedal(conn.p, (byte) 4, (byte) 10, false);
                randomTT(conn, (byte) 5, (byte) 6);
                randomTT(conn, (byte) 5, (byte) 6);
                randomTT(conn, (byte) 5, (byte) 6);
                randomTT(conn, (byte) 5, (byte) 6);
                randomTT(conn, (byte) 5, (byte) 6);
                Service.send_notice_box(conn, "Bạn đã nhận được quà");
                Admin.topEvent.remove(conn.p.name);
                break;
            }
            case 2: {
                ItemTemplate3 temp3 = ItemTemplate3.item.get(4641); // cánh
                Item3 it = new Item3();
                it.id = temp3.getId();
                it.name = temp3.getName();
                it.clazz = temp3.getClazz();
                it.type = temp3.getType();
                it.level = temp3.getLevel();
                it.icon = temp3.getIcon();
                it.op = temp3.getOp();
                it.color = 5;
                it.part = temp3.getPart();
                conn.p.item.add_item_inventory3(it);

                int id = 4812;
                if (conn.p.clazz <= 1) {
                    id = 4813;
                }
                temp3 = ItemTemplate3.item.get(id);// Thời trang rồng
                it = new Item3();
                it.id = temp3.getId();
                it.name = temp3.getName();
                it.clazz = temp3.getClazz();
                it.type = temp3.getType();
                it.level = temp3.getLevel();
                it.icon = temp3.getIcon();
                it.op = new ArrayList<>();
                it.op.add(new Option(7, 5000));
                it.op.add(new Option(8, 5000));
                it.op.add(new Option(9, 5000));
                it.op.add(new Option(10, 5000));
                it.op.add(new Option(11, 5000));
                it.op.add(new Option(27, 1500));
                it.op.add(new Option(-128, 500));

                it.color = 5;
                it.part = temp3.getPart();
                conn.p.item.add_item_inventory3(it);

                temp3 = ItemTemplate3.item.get(4617);// trứng rồng lửa
                it = new Item3();
                it.id = temp3.getId();
                it.name = temp3.getName();
                it.clazz = temp3.getClazz();
                it.type = temp3.getType();
                it.level = temp3.getLevel();
                it.icon = temp3.getIcon();
                it.op = temp3.getOp();
                it.color = 5;
                it.part = temp3.getPart();
                conn.p.item.add_item_inventory3(it);

                Admin.randomMedal(conn.p, (byte) 4, (byte) 8, false);
                randomTT(conn, (byte) 5, (byte) 7);
                randomTT(conn, (byte) 5, (byte) 7);
                randomTT(conn, (byte) 5, (byte) 7);
                Service.send_notice_box(conn, "Bạn đã nhận được quà");
                Admin.topEvent.remove(conn.p.name);
                break;
            }
            case 3: {
                ItemTemplate3 temp3 = ItemTemplate3.item.get(4641); // cánh
                Item3 it = new Item3();
                it.id = temp3.getId();
                it.name = temp3.getName();
                it.clazz = temp3.getClazz();
                it.type = temp3.getType();
                it.level = temp3.getLevel();
                it.icon = temp3.getIcon();
                it.op = temp3.getOp();
                it.color = 5;
                it.part = temp3.getPart();
                it.expiry_date = 6L * 30 * 24 * 60 * 60 * 1000 + System.currentTimeMillis();
                conn.p.item.add_item_inventory3(it);

                int id = 4812;
                if (conn.p.clazz <= 1) {
                    id = 4813;
                }
                temp3 = ItemTemplate3.item.get(id);// Thời trang rồng
                it = new Item3();
                it.id = temp3.getId();
                it.name = temp3.getName();
                it.clazz = temp3.getClazz();
                it.type = temp3.getType();
                it.level = temp3.getLevel();
                it.icon = temp3.getIcon();
                it.op = new ArrayList<>();
                it.op.add(new Option(7, 4000));
                it.op.add(new Option(8, 4000));
                it.op.add(new Option(9, 4000));
                it.op.add(new Option(10, 4000));
                it.op.add(new Option(11, 4000));
                it.op.add(new Option(27, 1500));
                it.op.add(new Option(-128, 500));

                it.color = 5;
                it.part = temp3.getPart();
                conn.p.item.add_item_inventory3(it);

                Admin.randomMedal(conn.p, (byte) 4, (byte) 6, false);
                randomTT(conn, (byte) 5, (byte) 5);
                Service.send_notice_box(conn, "Bạn đã nhận được quà");
                Admin.topEvent.remove(conn.p.name);
                break;
            }
        }
        Manager.gI().notifierBot.sendNotification(conn.p.name + " đã nhận quà top event");
    }

    public static String xemmocnap(Session conn) throws IOException {
        String currentUserId = String.valueOf(conn.id); // Assuming you have a method to get the current user's ID
        Integer tongnap = null;
        String tongnap1 = null;

        // lay tong nap va moc nap cua id dang dang nhap
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/hso?useSSL=false", "root",
                "");
                PreparedStatement pstmt = connection.prepareStatement("SELECT tongnap FROM account WHERE id = ?")) {
            pstmt.setString(1, currentUserId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                tongnap = rs.getInt("tongnap");

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        tongnap1 = String.valueOf(tongnap);
        // String.format("%s", tongnap1);
        double amount = Double.parseDouble(tongnap1);
        DecimalFormat formatter = new DecimalFormat("#,###");
        tongnap1 = formatter.format(amount);
        return tongnap1;

    }

    public static void quamocnap1(Session conn) throws IOException {
        String currentUserId = String.valueOf(conn.id); // Assuming you have a method to get the current user's ID
        Integer tongnap = null;
        Boolean mocnap1 = null;
        String tongnapString = null;
        int invent_able = conn.p.item.get_inventory_able();

        // lay tong nap va moc nap cua id dang dang nhap
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/hso?useSSL=false", "root",
                "");
                PreparedStatement pstmt = connection
                        .prepareStatement("SELECT tongnap,mocnap1 FROM account WHERE id = ?")) {
            pstmt.setString(1, currentUserId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                tongnap = rs.getInt("tongnap");
                mocnap1 = rs.getBoolean("mocnap1");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        double amount = Double.parseDouble(String.valueOf(tongnap));
        DecimalFormat formatter = new DecimalFormat("#,###");
        tongnapString = formatter.format(amount);

        // dieu kien nhan qua moc nap va chuyen qua cho player
        if (invent_able < 3) {
            Service.send_notice_box(conn, "kho đồ của bạn hiện không đủ vui lòng dọn trống 3 ô ");
        } else if (tongnap >= 200000 && mocnap1 == false) {
            // item4
            ItemTemplate4 x = ItemTemplate4.item.get(196);
            ItemTemplate7 y = ItemTemplate7.item.get(13);
            ItemTemplate4 z = ItemTemplate4.item.get(318);

            Message m = new Message(78);
            m.writer().writeUTF("mốc 1 -200k");
            m.writer().writeByte(3); // size
            // for (int i = 0; i < 3; i++) {
            m.writer().writeUTF(""); // name
            m.writer().writeShort(x.getIcon()); // icon
            m.writer().writeInt(20); // quantity
            m.writer().writeByte(4); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color
            //
            m.writer().writeUTF(""); // name
            m.writer().writeShort(y.getIcon()); // icon
            m.writer().writeInt(200); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color
            //
            m.writer().writeUTF(""); // name
            m.writer().writeShort(z.getIcon()); // icon
            m.writer().writeInt(3); // quantity
            m.writer().writeByte(4); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color
            // }
            m.writer().writeUTF("");
            m.writer().writeByte(1);
            m.writer().writeByte(0);
            conn.addmsg(m);
            m.cleanup();
            Item47 item1 = new Item47();
            item1.id = 14; // đá ba màu
            item1.quantity = 20;
            conn.p.item.add_item_inventory47(7, item1);
            // item 7
            Item47 item2 = new Item47();
            item2.id = 13; // cỏ 4 lá
            item2.quantity = 200;
            conn.p.item.add_item_inventory47(7, item2);
            Item47 item3 = new Item47();
            item3.id = 318; // ruong black firday
            item3.quantity = 3;
            conn.p.item.add_item_inventory47(4, item3);

            // update da nhan moc nap
            try (Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/hso?useSSL=false",
                    "root", "");
                    PreparedStatement pstmt = connection
                            .prepareStatement("update account set mocnap1=true WHERE id = ?")) {
                pstmt.setString(1, currentUserId);
                pstmt.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            // Service.send_notice_box(conn,"chúc mừng bạn đã hoàn thành mốc 1 -> 200,000");

        } else if (tongnap >= 200000 && mocnap1 == true) {
            Service.send_notice_box(conn, "Bạn đã nhận mốc nạp này");
        } else if (tongnap < 200000) {
            Service.send_notice_box(conn, "tổng nạp của bạn chưa đủ mốc: 200,000 -> " + tongnapString + "/200,000");
        }
    }

    public static void quamocnap2(Session conn) throws IOException {
        String currentUserId = String.valueOf(conn.id); // Assuming you have a method to get the current user's ID
        Integer tongnap = null;
        Boolean mocnap2 = null;
        String tongnapString = null;
        int invent_able = conn.p.item.get_inventory_able();

        // lay tong nap va moc nap cua id dang dang nhap
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/hso?useSSL=false", "root",
                "");
                PreparedStatement pstmt = connection
                        .prepareStatement("SELECT tongnap,mocnap2 FROM account WHERE id = ?")) {
            pstmt.setString(1, currentUserId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                tongnap = rs.getInt("tongnap");
                mocnap2 = rs.getBoolean("mocnap2");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        double amount = Double.parseDouble(String.valueOf(tongnap));
        DecimalFormat formatter = new DecimalFormat("#,###");
        tongnapString = formatter.format(amount);

        // dieu kien nhan qua moc nap va chuyen qua cho player
        if (invent_able < 6) {
            Service.send_notice_box(conn, "kho đồ của bạn hiện không đủ vui lòng dọn trống 5 ô ");
        } else if (tongnap >= 500000 && mocnap2 == false) {
            // item4

            Item47 item1 = new Item47();
            item1.id = 14; // đá ba màu
            item1.quantity = 50;
            conn.p.item.add_item_inventory47(7, item1);
            Item47 item2 = new Item47();
            item2.id = 206; // rương vàng
            item2.quantity = 500;
            conn.p.item.add_item_inventory47(4, item2);
            // item 7
            Item47 item3 = new Item47();
            item3.id = 318; // ruong black firday
            item3.quantity = 5;
            conn.p.item.add_item_inventory47(4, item3);
            // item3
            ItemTemplate3 temp3 = ItemTemplate3.item.get(4688);
            Item3 it = new Item3();
            it.id = temp3.getId();
            it.name = temp3.getName();
            it.clazz = temp3.getClazz();
            it.type = temp3.getType();
            it.level = temp3.getLevel();
            it.icon = temp3.getIcon();
            it.op = temp3.getOp();
            it.color = temp3.getColor();
            it.islock = true;
            it.part = temp3.getPart();
            conn.p.item.add_item_inventory3(it);

            ItemTemplate3 temp4 = ItemTemplate3.item.get(4793);
            Item3 it2 = new Item3();
            it2.id = temp4.getId();
            it2.name = temp4.getName();
            it2.clazz = temp4.getClazz();
            it2.type = temp4.getType();
            it2.level = temp4.getLevel();
            it2.icon = temp4.getIcon();
            it2.op = temp4.getOp();
            it2.color = temp4.getColor();
            it2.islock = true;
            it2.part = temp4.getPart();
            conn.p.item.add_item_inventory3(it2);

            ItemTemplate4 x = ItemTemplate4.item.get(196);
            ItemTemplate4 y = ItemTemplate4.item.get(206);
            ItemTemplate4 z = ItemTemplate4.item.get(318);

            Message m = new Message(78);
            m.writer().writeUTF("mốc 2 -500k");
            m.writer().writeByte(5); // size
            // for (int i = 0; i < 3; i++) {
            m.writer().writeUTF(""); // name
            m.writer().writeShort(x.getIcon()); // icon
            m.writer().writeInt(50); // quantity
            m.writer().writeByte(4); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color
            //
            m.writer().writeUTF(""); // name
            m.writer().writeShort(y.getIcon()); // icon
            m.writer().writeInt(500); // quantity
            m.writer().writeByte(4); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color
            //
            m.writer().writeUTF(""); // name
            m.writer().writeShort(z.getIcon()); // icon
            m.writer().writeInt(5); // quantity
            m.writer().writeByte(4); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color
            // }

            m.writer().writeUTF("");
            m.writer().writeShort(temp3.getIcon());
            m.writer().writeInt(1);
            m.writer().writeByte(3);
            m.writer().writeByte(0);
            m.writer().writeByte(4);

            m.writer().writeUTF("");
            m.writer().writeShort(temp4.getIcon());
            m.writer().writeInt(1);
            m.writer().writeByte(3);
            m.writer().writeByte(0);
            m.writer().writeByte(4);

            m.writer().writeUTF("");
            m.writer().writeByte(1);
            m.writer().writeByte(0);
            conn.addmsg(m);
            m.cleanup();

            // update da nhan moc nap
            try (Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/hso?useSSL=false",
                    "root", "");
                    PreparedStatement pstmt = connection
                            .prepareStatement("update account set mocnap2=true WHERE id = ?")) {
                pstmt.setString(1, currentUserId);
                pstmt.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            // Service.send_notice_box(conn,"chúc mừng bạn đã hoàn thành mốc nạp 2 ->
            // 500,000" );

        } else if (tongnap >= 500000 && mocnap2 == true) {
            Service.send_notice_box(conn, "Bạn đã nhận mốc nạp này");
        } else if (tongnap < 500000) {
            Service.send_notice_box(conn, "tổng nạp của bạn chưa đủ mốc: 500,000 -> " + tongnapString + "/500,000");
        }
    }

    public static void quamocnap3(Session conn) throws IOException {
        String currentUserId = String.valueOf(conn.id); // Assuming you have a method to get the current user's ID
        Integer tongnap = null;
        Boolean mocnap3 = null;
        String tongnapString = null;
        int invent_able = conn.p.item.get_inventory_able();

        // lay tong nap va moc nap cua id dang dang nhap
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/hso?useSSL=false", "root",
                "");
                PreparedStatement pstmt = connection
                        .prepareStatement("SELECT tongnap,mocnap3 FROM account WHERE id = ?")) {
            pstmt.setString(1, currentUserId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                tongnap = rs.getInt("tongnap");
                mocnap3 = rs.getBoolean("mocnap3");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        double amount = Double.parseDouble(String.valueOf(tongnap));
        DecimalFormat formatter = new DecimalFormat("#,###");
        tongnapString = formatter.format(amount);

        // dieu kien nhan qua moc nap va chuyen qua cho player
        if (invent_able < 6) {
            Service.send_notice_box(conn, "kho đồ của bạn hiện không đủ vui lòng dọn trống 6 ô ");
        } else if (tongnap >= 1000000 && mocnap3 == false) {
            // mondo
            // item4
            Item47 item1 = new Item47();
            item1.id = 14; // đá ba màu
            item1.quantity = 50;
            conn.p.item.add_item_inventory47(7, item1);
            Item47 item2 = new Item47();
            item2.id = 206; // rương vàng
            item2.quantity = 1000;
            conn.p.item.add_item_inventory47(4, item2);
            // item 7
            Item47 item3 = new Item47();
            item3.id = 318; // đá thạch anh cấp 3
            item3.quantity = 10;
            conn.p.item.add_item_inventory47(4, item3);
            Item47 item4 = new Item47();
            item4.id = 349; // đá kryton 1
            item4.quantity = 5;
            conn.p.item.add_item_inventory47(7, item4);
            // item 3
            ItemTemplate3 temp3 = ItemTemplate3.item.get(4716);// khau trang xanh
            Item3 it = new Item3();
            it.id = temp3.getId();
            it.name = temp3.getName();
            it.clazz = temp3.getClazz();
            it.type = temp3.getType();
            it.level = temp3.getLevel();
            it.icon = temp3.getIcon();
            it.op = temp3.getOp();
            it.color = temp3.getColor();
            it.islock = false;
            it.part = temp3.getPart();
            conn.p.item.add_item_inventory3(it);
            ItemTemplate3 temp4 = ItemTemplate3.item.get(4719);// tay nghe mong mo
            Item3 it2 = new Item3();
            it2.id = temp4.getId();
            it2.name = temp4.getName();
            it2.clazz = temp4.getClazz();
            it2.type = temp4.getType();
            it2.level = temp4.getLevel();
            it2.icon = temp4.getIcon();
            it2.op = temp4.getOp();
            it2.color = temp4.getColor();
            it2.islock = false;
            it2.part = temp4.getPart();
            conn.p.item.add_item_inventory3(it2);
            ItemTemplate4 x = ItemTemplate4.item.get(196);
            ItemTemplate4 y = ItemTemplate4.item.get(206);
            ItemTemplate4 z = ItemTemplate4.item.get(318);
            ItemTemplate7 v = ItemTemplate7.item.get(349);

            Message m = new Message(78);
            m.writer().writeUTF("mốc 3 -1000K");
            m.writer().writeByte(6); // size
            // for (int i = 0; i < 3; i++) {
            m.writer().writeUTF(""); // name
            m.writer().writeShort(x.getIcon()); // icon
            m.writer().writeInt(50); // quantity
            m.writer().writeByte(4); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color
            //
            m.writer().writeUTF(""); // name
            m.writer().writeShort(y.getIcon()); // icon
            m.writer().writeInt(1000); // quantity
            m.writer().writeByte(4); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color
            //
            m.writer().writeUTF(""); // name
            m.writer().writeShort(z.getIcon()); // icon
            m.writer().writeInt(10); // quantity
            m.writer().writeByte(4); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color

            m.writer().writeUTF(""); // name
            m.writer().writeShort(v.getIcon()); // icon
            m.writer().writeInt(5); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color

            m.writer().writeUTF("");
            m.writer().writeShort(temp3.getIcon());
            m.writer().writeInt(1);
            m.writer().writeByte(3);
            m.writer().writeByte(0);
            m.writer().writeByte(4);

            m.writer().writeUTF("");
            m.writer().writeShort(temp4.getIcon());
            m.writer().writeInt(1);
            m.writer().writeByte(3);
            m.writer().writeByte(0);
            m.writer().writeByte(4);

            m.writer().writeUTF("");
            m.writer().writeByte(1);
            m.writer().writeByte(0);
            conn.addmsg(m);
            m.cleanup();
            // update da nhan moc nap
            try (Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/hso?useSSL=false",
                    "root", "");
                    PreparedStatement pstmt = connection
                            .prepareStatement("update account set mocnap3=true WHERE id = ?")) {
                pstmt.setString(1, currentUserId);
                pstmt.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            // Service.send_notice_box(conn,"chúc mừng bạn đã hoàn thành mốc nạp 3 ->
            // 1,000,000");

        } else if (tongnap >= 1000000 && mocnap3 == true) {
            Service.send_notice_box(conn, "Bạn đã nhận mốc nạp này");
        } else if (tongnap < 1000000) {
            Service.send_notice_box(conn, "tổng nạp của bạn chưa đủ mốc: 1,000,000 -> " + tongnapString + "/1,000,000");
        }
    }

    public static void quamocnap4(Session conn) throws IOException {
        String currentUserId = String.valueOf(conn.id); // Assuming you have a method to get the current user's ID
        Integer tongnap = null;
        Boolean mocnap4 = null;
        String tongnapString = null;
        int invent_able = conn.p.item.get_inventory_able();

        // lay tong nap va moc nap cua id dang dang nhap
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/hso?useSSL=false", "root",
                "");
                PreparedStatement pstmt = connection
                        .prepareStatement("SELECT tongnap,mocnap4 FROM account WHERE id = ?")) {
            pstmt.setString(1, currentUserId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                tongnap = rs.getInt("tongnap");
                mocnap4 = rs.getBoolean("mocnap4");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        double amount = Double.parseDouble(String.valueOf(tongnap));
        DecimalFormat formatter = new DecimalFormat("#,###");
        tongnapString = formatter.format(amount);

        // dieu kien nhan qua moc nap va chuyen qua cho player
        if (invent_able < 6) {
            Service.send_notice_box(conn, "kho đồ của bạn hiện không đủ vui lòng dọn trống 6 ô ");
        } else if (tongnap >= 2000000 && mocnap4 == false) {
            // mondo
            // item4
            Item47 item1 = new Item47();
            item1.id = 14; // đá ba màu
            item1.quantity = 100;
            conn.p.item.add_item_inventory47(7, item1);
            // item 7
            Item47 item2 = new Item47();
            item2.id = 355; // hỗn nguyên 4
            item2.quantity = 10;
            conn.p.item.add_item_inventory47(7, item2);
            Item47 item3 = new Item47();
            item3.id = 45; // đục 3
            item3.quantity = 30;
            conn.p.item.add_item_inventory47(7, item3);
            Item47 item4 = new Item47();
            item4.id = 360; // khải hoàn 4
            item4.quantity = 10;
            conn.p.item.add_item_inventory47(7, item4);
            Item47 item5 = new Item47();
            item5.id = 349; // đá kryton 1
            item5.quantity = 10;
            conn.p.item.add_item_inventory47(7, item5);
            // item 3
            ItemTemplate3 temp3 = ItemTemplate3.item.get(4717);// khẩu trang đỏ
            Item3 it = new Item3();
            it.id = temp3.getId();
            it.name = temp3.getName();
            it.clazz = temp3.getClazz();
            it.type = temp3.getType();
            it.level = temp3.getLevel();
            it.icon = temp3.getIcon();
            it.op = temp3.getOp();
            it.color = temp3.getColor();
            it.islock = false;
            it.part = temp3.getPart();
            conn.p.item.add_item_inventory3(it);

            ItemTemplate4 x = ItemTemplate4.item.get(196);
            ItemTemplate7 y = ItemTemplate7.item.get(355);
            ItemTemplate7 z = ItemTemplate7.item.get(349);
            ItemTemplate7 v = ItemTemplate7.item.get(360);
            ItemTemplate7 b = ItemTemplate7.item.get(45);

            Message m = new Message(78);
            m.writer().writeUTF("mốc 4 -2000K");
            m.writer().writeByte(6); // size
            // for (int i = 0; i < 3; i++) {
            m.writer().writeUTF(""); // name
            m.writer().writeShort(x.getIcon()); // icon
            m.writer().writeInt(100); // quantity
            m.writer().writeByte(4); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color
            //
            m.writer().writeUTF(""); // name
            m.writer().writeShort(y.getIcon()); // icon
            m.writer().writeInt(10); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color
            //
            m.writer().writeUTF(""); // name
            m.writer().writeShort(z.getIcon()); // icon
            m.writer().writeInt(10); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color

            m.writer().writeUTF(""); // name
            m.writer().writeShort(v.getIcon()); // icon
            m.writer().writeInt(10); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color

            m.writer().writeUTF(""); // name
            m.writer().writeShort(b.getIcon()); // icon
            m.writer().writeInt(30); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color

            m.writer().writeUTF("");
            m.writer().writeShort(temp3.getIcon());
            m.writer().writeInt(1);
            m.writer().writeByte(3);
            m.writer().writeByte(0);
            m.writer().writeByte(4);

            m.writer().writeUTF("");
            m.writer().writeByte(1);
            m.writer().writeByte(0);
            conn.addmsg(m);
            m.cleanup();

            // update da nhan moc nap
            try (Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/hso?useSSL=false",
                    "root", "");
                    PreparedStatement pstmt = connection
                            .prepareStatement("update account set mocnap4=true WHERE id = ?")) {
                pstmt.setString(1, currentUserId);
                pstmt.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            // Service.send_notice_box(conn,"chúc mừng bạn đã hoàn thành mốc nạp 4 ->
            // 2,000,000");

        } else if (tongnap >= 2000000 && mocnap4 == true) {
            Service.send_notice_box(conn, "Bạn đã nhận mốc nạp này");
        } else if (tongnap < 2000000) {
            Service.send_notice_box(conn, "tổng nạp của bạn chưa đủ mốc: 2,000,000 -> " + tongnapString + "/2,000,000");
        }
    }

    public static void quamocnap5(Session conn) throws IOException {
        String currentUserId = String.valueOf(conn.id); // Assuming you have a method to get the current user's ID
        Integer tongnap = null;
        Boolean mocnap5 = null;
        String tongnapString = null;
        int invent_able = conn.p.item.get_inventory_able();

        // lay tong nap va moc nap cua id dang dang nhap
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/hso?useSSL=false", "root",
                "");
                PreparedStatement pstmt = connection
                        .prepareStatement("SELECT tongnap,mocnap5 FROM account WHERE id = ?")) {
            pstmt.setString(1, currentUserId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                tongnap = rs.getInt("tongnap");
                mocnap5 = rs.getBoolean("mocnap5");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        double amount = Double.parseDouble(String.valueOf(tongnap));
        DecimalFormat formatter = new DecimalFormat("#,###");
        tongnapString = formatter.format(amount);

        // dieu kien nhan qua moc nap va chuyen qua cho player
        if (invent_able < 9) {
            Service.send_notice_box(conn, "kho đồ của bạn hiện không đủ vui lòng dọn trống 9 ô ");
        } else if (tongnap >= 3500000 && mocnap5 == false) {
            // mondo
            // item4
            // item 7
            Item47 item2 = new Item47();
            item2.id = 370; // phong ma 4
            item2.quantity = 10;
            conn.p.item.add_item_inventory47(7, item2);
            Item47 item4 = new Item47();
            item4.id = 365; // ngọc lục bảo 4
            item4.quantity = 10;
            conn.p.item.add_item_inventory47(7, item4);
            Item47 item5 = new Item47();
            item5.id = 371; // phong ma 5
            item5.quantity = 5;
            conn.p.item.add_item_inventory47(7, item5);
            Item47 item6 = new Item47();
            item6.id = 366; // ngọc lục bảo 5
            item6.quantity = 5;
            conn.p.item.add_item_inventory47(7, item6);
            Item47 item3 = new Item47();
            item3.id = 45; // đục 3
            item3.quantity = 50;
            conn.p.item.add_item_inventory47(7, item3);
            Item47 item7 = new Item47();
            item7.id = 350; // đá krypton 2
            item7.quantity = 5;
            conn.p.item.add_item_inventory47(7, item7);
            Item47 item8 = new Item47();
            item8.id = 471; // đá hoả tinh
            item8.quantity = 5;
            conn.p.item.add_item_inventory47(7, item8);
            // item3
            ItemTemplate3 temp3 = ItemTemplate3.item.get(4718);// tai nghe phong cách
            Item3 it = new Item3();
            it.id = temp3.getId();
            it.name = temp3.getName();
            it.clazz = temp3.getClazz();
            it.type = temp3.getType();
            it.level = temp3.getLevel();
            it.icon = temp3.getIcon();
            it.op = temp3.getOp();
            it.color = temp3.getColor();
            it.islock = false;
            it.part = temp3.getPart();
            conn.p.item.add_item_inventory3(it);
            ItemTemplate3 temp4 = ItemTemplate3.item.get(4762);// trứng thiên thần
            Item3 it2 = new Item3();
            it2.id = temp4.getId();
            it2.name = temp4.getName();
            it2.clazz = temp4.getClazz();
            it2.type = temp4.getType();
            it2.level = temp4.getLevel();
            it2.icon = temp4.getIcon();
            it2.op = temp4.getOp();
            it2.color = temp4.getColor();
            it2.islock = false;
            it2.part = temp4.getPart();
            conn.p.item.add_item_inventory3(it2);

            ItemTemplate7 x1 = ItemTemplate7.item.get(370);
            ItemTemplate7 x2 = ItemTemplate7.item.get(365);
            ItemTemplate7 x3 = ItemTemplate7.item.get(371);
            ItemTemplate7 x4 = ItemTemplate7.item.get(366);
            ItemTemplate7 x5 = ItemTemplate7.item.get(45);
            ItemTemplate7 x6 = ItemTemplate7.item.get(350);
            ItemTemplate7 x7 = ItemTemplate7.item.get(471);

            int quant1_ = 10;
            int quant2_ = 10;
            int quant3_ = 5;
            int quant4_ = 5;
            int quant5_ = 50;
            int quant6_ = 5;
            int quant7_ = 5;
            Message m = new Message(78);
            m.writer().writeUTF("mốc 5 -3500K");
            m.writer().writeByte(9); // size
            // for (int i = 0; i < 3; i++) {
            m.writer().writeUTF(""); // name
            m.writer().writeShort(x1.getIcon()); // icon
            m.writer().writeInt(quant1_); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color
            //
            m.writer().writeUTF(""); // name
            m.writer().writeShort(x2.getIcon()); // icon
            m.writer().writeInt(quant2_); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color
            //
            m.writer().writeUTF(""); // name
            m.writer().writeShort(x3.getIcon()); // icon
            m.writer().writeInt(quant3_); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color

            m.writer().writeUTF(""); // name
            m.writer().writeShort(x4.getIcon()); // icon
            m.writer().writeInt(quant4_); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color

            m.writer().writeUTF(""); // name
            m.writer().writeShort(x5.getIcon()); // icon
            m.writer().writeInt(quant5_); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color

            m.writer().writeUTF(""); // name
            m.writer().writeShort(x6.getIcon()); // icon
            m.writer().writeInt(quant6_); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color

            m.writer().writeUTF(""); // name
            m.writer().writeShort(x7.getIcon()); // icon
            m.writer().writeInt(quant7_); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color

            m.writer().writeUTF("");
            m.writer().writeShort(temp3.getIcon());
            m.writer().writeInt(1);
            m.writer().writeByte(3);
            m.writer().writeByte(0);
            m.writer().writeByte(4);

            m.writer().writeUTF("");
            m.writer().writeShort(temp4.getIcon());
            m.writer().writeInt(1);
            m.writer().writeByte(3);
            m.writer().writeByte(0);
            m.writer().writeByte(4);

            m.writer().writeUTF("");
            m.writer().writeByte(1);
            m.writer().writeByte(0);
            conn.addmsg(m);
            m.cleanup();
            // update da nhan moc nap
            try (Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/hso?useSSL=false",
                    "root", "");
                    PreparedStatement pstmt = connection
                            .prepareStatement("update account set mocnap5=true WHERE id = ?")) {
                pstmt.setString(1, currentUserId);
                pstmt.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            // Service.send_notice_box(conn,"chúc mừng bạn đã hoàn thành mốc nạp 5 ->
            // 3,500,000");

        } else if (tongnap >= 3500000 && mocnap5 == true) {
            Service.send_notice_box(conn, "Bạn đã nhận mốc nạp này");
        } else if (tongnap < 3500000) {
            Service.send_notice_box(conn, "tổng nạp của bạn chưa đủ mốc: 3,500,000 -> " + tongnapString + "/3,500,000");
        }
    }

    public static void quamocnap6(Session conn) throws IOException {
        String currentUserId = String.valueOf(conn.id); // Assuming you have a method to get the current user's ID
        Integer tongnap = null;
        Boolean mocnap6 = null;
        String tongnapString = null;
        int invent_able = conn.p.item.get_inventory_able();

        // lay tong nap va moc nap cua id dang dang nhap
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/hso?useSSL=false", "root",
                "");
                PreparedStatement pstmt = connection
                        .prepareStatement("SELECT tongnap,mocnap6 FROM account WHERE id = ?")) {
            pstmt.setString(1, currentUserId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                tongnap = rs.getInt("tongnap");
                mocnap6 = rs.getBoolean("mocnap6");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        double amount = Double.parseDouble(String.valueOf(tongnap));
        DecimalFormat formatter = new DecimalFormat("#,###");
        tongnapString = formatter.format(amount);

        // dieu kien nhan qua moc nap va chuyen qua cho player
        if (invent_able < 7) {
            Service.send_notice_box(conn, "kho đồ của bạn hiện không đủ vui lòng dọn trống 7 ô ");
        } else if (tongnap >= 5000000 && mocnap6 == false) {
            // mondo
            // item4

            // item 7

            Item47 item5 = new Item47();
            item5.id = 381; // tâm linh 5
            item5.quantity = 10;
            conn.p.item.add_item_inventory47(7, item5);
            Item47 item6 = new Item47();
            item6.id = 361; // khải hoàn 5
            item6.quantity = 10;
            conn.p.item.add_item_inventory47(7, item6);
            Item47 item7 = new Item47();
            item7.id = 356; // hỗn nguyên 5
            item7.quantity = 10;
            conn.p.item.add_item_inventory47(7, item7);
            Item47 item3 = new Item47();
            item3.id = 45; // đục 3
            item3.quantity = 100;
            conn.p.item.add_item_inventory47(7, item3);
            Item47 item8 = new Item47();
            item8.id = 350; // đá krypton 2
            item8.quantity = 10;
            conn.p.item.add_item_inventory47(7, item8);
            Item47 item9 = new Item47();
            item9.id = 471; // đá hoả tinh
            item9.quantity = 10;
            conn.p.item.add_item_inventory47(7, item9);
            // item3
            ItemTemplate3 temp3 = ItemTemplate3.item.get(4708);// trứng dê
            Item3 it = new Item3();
            it.id = temp3.getId();
            it.name = temp3.getName();
            it.clazz = temp3.getClazz();
            it.type = temp3.getType();
            it.level = temp3.getLevel();
            it.icon = temp3.getIcon();
            it.op = temp3.getOp();
            it.color = temp3.getColor();
            it.islock = false;
            it.part = temp3.getPart();
            conn.p.item.add_item_inventory3(it);

            ItemTemplate7 x1 = ItemTemplate7.item.get(381);
            ItemTemplate7 x2 = ItemTemplate7.item.get(361);
            ItemTemplate7 x3 = ItemTemplate7.item.get(356);
            ItemTemplate7 x4 = ItemTemplate7.item.get(45);
            ItemTemplate7 x5 = ItemTemplate7.item.get(350);
            ItemTemplate7 x6 = ItemTemplate7.item.get(471);

            int quant1_ = 10;
            int quant2_ = 10;
            int quant3_ = 10;
            int quant4_ = 100;
            int quant5_ = 10;
            int quant6_ = 10;

            Message m = new Message(78);
            m.writer().writeUTF("mốc 6 -5000k");
            m.writer().writeByte(7); // size
            // for (int i = 0; i < 3; i++) {
            m.writer().writeUTF(""); // name
            m.writer().writeShort(x1.getIcon()); // icon
            m.writer().writeInt(quant1_); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color
            //
            m.writer().writeUTF(""); // name
            m.writer().writeShort(x2.getIcon()); // icon
            m.writer().writeInt(quant2_); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color
            //
            m.writer().writeUTF(""); // name
            m.writer().writeShort(x3.getIcon()); // icon
            m.writer().writeInt(quant3_); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color

            m.writer().writeUTF(""); // name
            m.writer().writeShort(x4.getIcon()); // icon
            m.writer().writeInt(quant4_); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color

            m.writer().writeUTF(""); // name
            m.writer().writeShort(x5.getIcon()); // icon
            m.writer().writeInt(quant5_); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color

            m.writer().writeUTF(""); // name
            m.writer().writeShort(x6.getIcon()); // icon
            m.writer().writeInt(quant6_); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color

            m.writer().writeUTF("");
            m.writer().writeShort(temp3.getIcon());
            m.writer().writeInt(1);
            m.writer().writeByte(3);
            m.writer().writeByte(0);
            m.writer().writeByte(4);

            m.writer().writeUTF("");
            m.writer().writeByte(1);
            m.writer().writeByte(0);
            conn.addmsg(m);
            m.cleanup();
            // update da nhan moc nap
            try (Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/hso?useSSL=false",
                    "root", "");
                    PreparedStatement pstmt = connection
                            .prepareStatement("update account set mocnap6=true WHERE id = ?")) {
                pstmt.setString(1, currentUserId);
                pstmt.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            // Service.send_notice_box(conn,"chúc mừng bạn đã hoàn thành mốc nạp 6 ->
            // 5,000,000");

        } else if (tongnap >= 5000000 && mocnap6 == true) {
            Service.send_notice_box(conn, "Bạn đã nhận mốc nạp này");
        } else if (tongnap < 5000000) {
            Service.send_notice_box(conn, "tổng nạp của bạn chưa đủ mốc: 5,000,000 -> " + tongnapString + "/5,000,000");
        }
    }

    public static void quamocnap7(Session conn) throws IOException {
        String currentUserId = String.valueOf(conn.id); // Assuming you have a method to get the current user's ID
        Integer tongnap = null;
        Boolean mocnap7 = null;
        String tongnapString = null;
        int invent_able = conn.p.item.get_inventory_able();

        // lay tong nap va moc nap cua id dang dang nhap
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/hso?useSSL=false", "root",
                "");
                PreparedStatement pstmt = connection
                        .prepareStatement("SELECT tongnap,mocnap7 FROM account WHERE id = ?")) {
            pstmt.setString(1, currentUserId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                tongnap = rs.getInt("tongnap");
                mocnap7 = rs.getBoolean("mocnap7");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        double amount = Double.parseDouble(String.valueOf(tongnap));
        DecimalFormat formatter = new DecimalFormat("#,###");
        tongnapString = formatter.format(amount);

        // dieu kien nhan qua moc nap va chuyen qua cho player
        if (invent_able < 9) {
            Service.send_notice_box(conn, "kho đồ của bạn hiện không đủ vui lòng dọn trống 9 ô ");
        } else if (tongnap >= 7000000 && mocnap7 == false) {
            // mondo
            // item 7

            Item47 item5 = new Item47();
            item5.id = 381; // tâm linh 5
            item5.quantity = 15;
            conn.p.item.add_item_inventory47(7, item5);
            Item47 item6 = new Item47();
            item6.id = 361; // khải hoàn 5
            item6.quantity = 15;
            conn.p.item.add_item_inventory47(7, item6);
            Item47 item7 = new Item47();
            item7.id = 356; // hỗn nguyên 5
            item7.quantity = 15;
            conn.p.item.add_item_inventory47(7, item7);
            Item47 item3 = new Item47();
            item3.id = 45; // đục 3
            item3.quantity = 100;
            conn.p.item.add_item_inventory47(7, item3);
            Item47 item8 = new Item47();
            item8.id = 350; // đá krypton 2
            item8.quantity = 15;
            conn.p.item.add_item_inventory47(7, item8);
            Item47 item9 = new Item47();
            item9.id = 471; // đá hoả tinh
            item9.quantity = 15;
            conn.p.item.add_item_inventory47(7, item9);
            Item47 item10 = new Item47();
            item10.id = 248; // đá hoả tinh
            item10.quantity = 1;
            conn.p.item.add_item_inventory47(4, item10);
            // item3
            ItemTemplate3 temp3 = ItemTemplate3.item.get(4730);// gậy mặt trăng hoặc gậy trái tim
            Item3 it = new Item3();
            it.id = temp3.getId();
            it.name = temp3.getName();
            it.clazz = temp3.getClazz();
            it.type = temp3.getType();
            it.level = temp3.getLevel();
            it.icon = temp3.getIcon();
            it.op = temp3.getOp();
            it.color = temp3.getColor();
            it.islock = false;
            it.part = temp3.getPart();
            conn.p.item.add_item_inventory3(it);
            ItemTemplate3 temp4 = ItemTemplate3.item.get(4731);// gậy mặt trăng hoặc gậy trái tim
            Item3 it2 = new Item3();
            it2.id = temp4.getId();
            it2.name = temp4.getName();
            it2.clazz = temp4.getClazz();
            it2.type = temp4.getType();
            it2.level = temp4.getLevel();
            it2.icon = temp4.getIcon();
            it2.op = temp4.getOp();
            it2.color = temp4.getColor();
            it2.islock = false;
            it2.part = temp4.getPart();
            conn.p.item.add_item_inventory3(it2);

            ItemTemplate7 x1 = ItemTemplate7.item.get(381);
            ItemTemplate7 x2 = ItemTemplate7.item.get(361);
            ItemTemplate7 x3 = ItemTemplate7.item.get(356);
            ItemTemplate7 x4 = ItemTemplate7.item.get(45);
            ItemTemplate7 x5 = ItemTemplate7.item.get(350);
            ItemTemplate7 x6 = ItemTemplate7.item.get(471);
            ItemTemplate4 x7 = ItemTemplate4.item.get(248);

            int quant1_ = 15;
            int quant2_ = 15;
            int quant3_ = 15;
            int quant4_ = 100;
            int quant5_ = 15;
            int quant6_ = 15;
            int quant7_ = 1;

            Message m = new Message(78);
            m.writer().writeUTF("mốc 7 -7000k");
            m.writer().writeByte(8); // size
            // for (int i = 0; i < 3; i++) {
            m.writer().writeUTF(""); // name
            m.writer().writeShort(x1.getIcon()); // icon
            m.writer().writeInt(quant1_); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color
            //
            m.writer().writeUTF(""); // name
            m.writer().writeShort(x2.getIcon()); // icon
            m.writer().writeInt(quant2_); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color
            //
            m.writer().writeUTF(""); // name
            m.writer().writeShort(x3.getIcon()); // icon
            m.writer().writeInt(quant3_); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color

            m.writer().writeUTF(""); // name
            m.writer().writeShort(x4.getIcon()); // icon
            m.writer().writeInt(quant4_); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color

            m.writer().writeUTF(""); // name
            m.writer().writeShort(x5.getIcon()); // icon
            m.writer().writeInt(quant5_); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color

            m.writer().writeUTF(""); // name
            m.writer().writeShort(x6.getIcon()); // icon
            m.writer().writeInt(quant6_); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color

            m.writer().writeUTF(""); // name
            m.writer().writeShort(x7.getIcon()); // icon
            m.writer().writeInt(quant7_); // quantity
            m.writer().writeByte(4); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color

            m.writer().writeUTF("");
            m.writer().writeShort(temp3.getIcon());
            m.writer().writeInt(1);
            m.writer().writeByte(3);
            m.writer().writeByte(0);
            m.writer().writeByte(4);

            m.writer().writeUTF("");
            m.writer().writeShort(temp4.getIcon());
            m.writer().writeInt(1);
            m.writer().writeByte(3);
            m.writer().writeByte(0);
            m.writer().writeByte(4);

            m.writer().writeUTF("");
            m.writer().writeByte(1);
            m.writer().writeByte(0);
            conn.addmsg(m);
            m.cleanup();
            // update da nhan moc nap
            try (Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/hso?useSSL=false",
                    "root", "");
                    PreparedStatement pstmt = connection
                            .prepareStatement("update account set mocnap7=true WHERE id = ?")) {
                pstmt.setString(1, currentUserId);
                pstmt.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            // Service.send_notice_box(conn,"chúc mừng bạn đã hoàn thành mốc nạp 7 ->
            // 7,000,000");

        } else if (tongnap >= 7000000 && mocnap7 == true) {
            Service.send_notice_box(conn, "Bạn đã nhận mốc nạp này");
        } else if (tongnap < 7000000) {
            Service.send_notice_box(conn, "tổng nạp của bạn chưa đủ mốc: 7,000,000 -> " + tongnapString + "/7,000,000");
        }
    }

    public static void quamocnap8(Session conn) throws IOException {
        String currentUserId = String.valueOf(conn.id); // Assuming you have a method to get the current user's ID
        Integer tongnap = null;
        Boolean mocnap8 = null;
        String tongnapString = null;
        int invent_able = conn.p.item.get_inventory_able();

        // lay tong nap va moc nap cua id dang dang nhap
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/hso?useSSL=false", "root",
                "");
                PreparedStatement pstmt = connection
                        .prepareStatement("SELECT tongnap,mocnap8 FROM account WHERE id = ?")) {
            pstmt.setString(1, currentUserId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                tongnap = rs.getInt("tongnap");
                mocnap8 = rs.getBoolean("mocnap8");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        double amount = Double.parseDouble(String.valueOf(tongnap));
        DecimalFormat formatter = new DecimalFormat("#,###");
        tongnapString = formatter.format(amount);
        // dieu kien nhan qua moc nap va chuyen qua cho player
        if (invent_able < 8) {
            Service.send_notice_box(conn, "kho đồ của bạn hiện không đủ vui lòng dọn trống 8 ô ");
        } else if (tongnap >= 10000000 && mocnap8 == false) {
            // mondo
            // item 4
            Item47 item10 = new Item47();
            item10.id = 317; // xe trượt tuyết
            item10.quantity = 1;
            conn.p.item.add_item_inventory47(4, item10);
            // item 7
            Item47 item5 = new Item47();
            item5.id = 381; // tâm linh 5
            item5.quantity = 20;
            conn.p.item.add_item_inventory47(7, item5);
            Item47 item6 = new Item47();
            item6.id = 361; // khải hoàn 5
            item6.quantity = 20;
            conn.p.item.add_item_inventory47(7, item6);
            Item47 item7 = new Item47();
            item7.id = 356; // hỗn nguyên 5
            item7.quantity = 20;
            conn.p.item.add_item_inventory47(7, item7);
            Item47 item3 = new Item47();
            item3.id = 45; // đục 3
            item3.quantity = 100;
            conn.p.item.add_item_inventory47(7, item3);
            Item47 item8 = new Item47();
            item8.id = 350; // đá krypton 2
            item8.quantity = 10;
            conn.p.item.add_item_inventory47(7, item8);
            Item47 item9 = new Item47();
            item9.id = 471; // đá hoả tinh
            item9.quantity = 10;
            conn.p.item.add_item_inventory47(7, item9);
            // item3
            ItemTemplate3 temp3 = ItemTemplate3.item.get(4710);// gay tuyet
            Item3 it = new Item3();
            it.id = temp3.getId();
            it.name = temp3.getName();
            it.clazz = temp3.getClazz();
            it.type = temp3.getType();
            it.level = temp3.getLevel();
            it.icon = temp3.getIcon();
            it.op = temp3.getOp();
            it.color = temp3.getColor();
            it.islock = false;
            it.part = temp3.getPart();
            conn.p.item.add_item_inventory3(it);

            ItemTemplate7 x1 = ItemTemplate7.item.get(381);
            ItemTemplate7 x2 = ItemTemplate7.item.get(361);
            ItemTemplate7 x3 = ItemTemplate7.item.get(356);
            ItemTemplate7 x4 = ItemTemplate7.item.get(45);
            ItemTemplate7 x5 = ItemTemplate7.item.get(350);
            ItemTemplate7 x6 = ItemTemplate7.item.get(471);
            ItemTemplate4 x7 = ItemTemplate4.item.get(317);
            int quant1_ = 20;
            int quant2_ = 20;
            int quant3_ = 20;
            int quant4_ = 100;
            int quant5_ = 10;
            int quant6_ = 10;
            int quant7_ = 1;

            Message m = new Message(78);
            m.writer().writeUTF("mốc 8 -10000k");
            m.writer().writeByte(8); // size
            // for (int i = 0; i < 3; i++) {
            m.writer().writeUTF(""); // name
            m.writer().writeShort(x1.getIcon()); // icon
            m.writer().writeInt(quant1_); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color
            //
            m.writer().writeUTF(""); // name
            m.writer().writeShort(x2.getIcon()); // icon
            m.writer().writeInt(quant2_); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color
            //
            m.writer().writeUTF(""); // name
            m.writer().writeShort(x3.getIcon()); // icon
            m.writer().writeInt(quant3_); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color

            m.writer().writeUTF(""); // name
            m.writer().writeShort(x4.getIcon()); // icon
            m.writer().writeInt(quant4_); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color

            m.writer().writeUTF(""); // name
            m.writer().writeShort(x5.getIcon()); // icon
            m.writer().writeInt(quant5_); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color

            m.writer().writeUTF(""); // name
            m.writer().writeShort(x6.getIcon()); // icon
            m.writer().writeInt(quant6_); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color

            m.writer().writeUTF(""); // name
            m.writer().writeShort(x7.getIcon()); // icon
            m.writer().writeInt(quant7_); // quantity
            m.writer().writeByte(4); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color

            m.writer().writeUTF("");
            m.writer().writeShort(temp3.getIcon());
            m.writer().writeInt(1);
            m.writer().writeByte(3);
            m.writer().writeByte(0);
            m.writer().writeByte(4);

            m.writer().writeUTF("");
            m.writer().writeByte(1);
            m.writer().writeByte(0);
            conn.addmsg(m);
            m.cleanup();
            // update da nhan moc nap
            try (Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/hso?useSSL=false",
                    "root", "");
                    PreparedStatement pstmt = connection
                            .prepareStatement("update account set mocnap8=true WHERE id = ?")) {
                pstmt.setString(1, currentUserId);
                pstmt.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            // Service.send_notice_box(conn,"chúc mừng bạn đã hoàn thành mốc nạp 8 ->
            // 10,000,000");

        } else if (tongnap >= 10000000 && mocnap8 == true) {
            Service.send_notice_box(conn, "Bạn đã nhận mốc nạp này");
        } else if (tongnap < 10000000) {
            Service.send_notice_box(conn,
                    "tổng nạp của bạn chưa đủ mốc: 10,000,000 -> " + tongnapString + "/10,000,000");
        }
    }

    public static void quamocnap9(Session conn) throws IOException {
        String currentUserId = String.valueOf(conn.id); // Assuming you have a method to get the current user's ID
        Integer tongnap = null;
        Boolean mocnap9 = null;
        String tongnapString = null;
        int invent_able = conn.p.item.get_inventory_able();

        // lay tong nap va moc nap cua id dang dang nhap
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/hso?useSSL=false", "root",
                "");
                PreparedStatement pstmt = connection
                        .prepareStatement("SELECT tongnap,mocnap9 FROM account WHERE id = ?")) {
            pstmt.setString(1, currentUserId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                tongnap = rs.getInt("tongnap");
                mocnap9 = rs.getBoolean("mocnap9");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        double amount = Double.parseDouble(String.valueOf(tongnap));
        DecimalFormat formatter = new DecimalFormat("#,###");
        tongnapString = formatter.format(amount);

        // dieu kien nhan qua moc nap va chuyen qua cho player
        if (invent_able < 9) {
            Service.send_notice_box(conn, "kho đồ của bạn hiện không đủ vui lòng dọn trống 9 ô ");
        } else if (tongnap >= 12000000 && mocnap9 == false) {
            // mondo
            // item 4
            Item47 item10 = new Item47();
            item10.id = 316; // moto ma tốc độ
            item10.quantity = 1;
            conn.p.item.add_item_inventory47(4, item10);
            // item 7
            Item47 item5 = new Item47();
            item5.id = 381; // tâm linh 5
            item5.quantity = 20;
            conn.p.item.add_item_inventory47(7, item5);
            Item47 item6 = new Item47();
            item6.id = 361; // khải hoàn 5
            item6.quantity = 20;
            conn.p.item.add_item_inventory47(7, item6);
            Item47 item7 = new Item47();
            item7.id = 356; // hỗn nguyên 5
            item7.quantity = 20;
            conn.p.item.add_item_inventory47(7, item7);
            Item47 item3 = new Item47();
            item3.id = 45; // đục 3
            item3.quantity = 150;
            conn.p.item.add_item_inventory47(7, item3);
            Item47 item8 = new Item47();
            item8.id = 351; // đá krypton 3
            item8.quantity = 5;
            conn.p.item.add_item_inventory47(7, item8);
            Item47 item9 = new Item47();
            item9.id = 471; // đá hoả tinh
            item9.quantity = 10;
            conn.p.item.add_item_inventory47(7, item9);
            // item 3
            ItemTemplate3 temp3 = ItemTemplate3.item.get(4810);//
            Item3 it = new Item3();
            it.id = temp3.getId();
            it.name = temp3.getName();
            it.clazz = temp3.getClazz();
            it.type = temp3.getType();
            it.level = temp3.getLevel();
            it.icon = temp3.getIcon();
            it.op = temp3.getOp();
            it.color = temp3.getColor();
            it.islock = false;
            it.part = temp3.getPart();
            conn.p.item.add_item_inventory3(it);
            ItemTemplate3 temp4 = ItemTemplate3.item.get(4811);// mặt nạ ma tốc độ
            Item3 it2 = new Item3();
            it2.id = temp4.getId();
            it2.name = temp4.getName();
            it2.clazz = temp4.getClazz();
            it2.type = temp4.getType();
            it2.level = temp4.getLevel();
            it2.icon = temp4.getIcon();
            it2.op = temp4.getOp();
            it2.color = temp4.getColor();
            it2.islock = false;
            it2.part = temp4.getPart();
            conn.p.item.add_item_inventory3(it2);

            ItemTemplate7 x1 = ItemTemplate7.item.get(381);
            ItemTemplate7 x2 = ItemTemplate7.item.get(361);
            ItemTemplate7 x3 = ItemTemplate7.item.get(356);
            ItemTemplate7 x4 = ItemTemplate7.item.get(45);
            ItemTemplate7 x5 = ItemTemplate7.item.get(351);
            ItemTemplate7 x6 = ItemTemplate7.item.get(471);
            ItemTemplate4 x7 = ItemTemplate4.item.get(316);
            int quant1_ = 20;
            int quant2_ = 20;
            int quant3_ = 20;
            int quant4_ = 150;
            int quant5_ = 5;
            int quant6_ = 10;
            int quant7_ = 1;

            Message m = new Message(78);
            m.writer().writeUTF("mốc 9 -12000k");
            m.writer().writeByte(9); // size
            // for (int i = 0; i < 3; i++) {
            m.writer().writeUTF(""); // name
            m.writer().writeShort(x1.getIcon()); // icon
            m.writer().writeInt(quant1_); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color
            //
            m.writer().writeUTF(""); // name
            m.writer().writeShort(x2.getIcon()); // icon
            m.writer().writeInt(quant2_); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color
            //
            m.writer().writeUTF(""); // name
            m.writer().writeShort(x3.getIcon()); // icon
            m.writer().writeInt(quant3_); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color

            m.writer().writeUTF(""); // name
            m.writer().writeShort(x4.getIcon()); // icon
            m.writer().writeInt(quant4_); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color

            m.writer().writeUTF(""); // name
            m.writer().writeShort(x5.getIcon()); // icon
            m.writer().writeInt(quant5_); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color

            m.writer().writeUTF(""); // name
            m.writer().writeShort(x6.getIcon()); // icon
            m.writer().writeInt(quant6_); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color

            m.writer().writeUTF(""); // name
            m.writer().writeShort(x7.getIcon()); // icon
            m.writer().writeInt(quant7_); // quantity
            m.writer().writeByte(4); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color

            m.writer().writeUTF("");
            m.writer().writeShort(temp3.getIcon());
            m.writer().writeInt(1);
            m.writer().writeByte(3);
            m.writer().writeByte(0);
            m.writer().writeByte(4);

            m.writer().writeUTF("");
            m.writer().writeShort(temp4.getIcon());
            m.writer().writeInt(1);
            m.writer().writeByte(3);
            m.writer().writeByte(0);
            m.writer().writeByte(4);

            m.writer().writeUTF("");
            m.writer().writeByte(1);
            m.writer().writeByte(0);
            conn.addmsg(m);
            m.cleanup();
            // update da nhan moc nap
            try (Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/hso?useSSL=false",
                    "root", "");
                    PreparedStatement pstmt = connection
                            .prepareStatement("update account set mocnap9=true WHERE id = ?")) {
                pstmt.setString(1, currentUserId);
                pstmt.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            // Service.send_notice_box(conn,"chúc mừng bạn đã hoàn thành mốc nạp 9 ->
            // 12,000,000");

        } else if (tongnap >= 12000000 && mocnap9 == true) {
            Service.send_notice_box(conn, "Bạn đã nhận mốc nạp này");
        } else if (tongnap < 12000000) {
            Service.send_notice_box(conn,
                    "tổng nạp của bạn chưa đủ mốc: 12,000,000 -> " + tongnapString + "/12,000,000");
        }
    }

    public static void quamocnap10(Session conn) throws IOException {
        String currentUserId = String.valueOf(conn.id); // Assuming you have a method to get the current user's ID
        Integer tongnap = null;
        Boolean mocnap10 = null;
        String tongnapString = null;
        int invent_able = conn.p.item.get_inventory_able();

        // lay tong nap va moc nap cua id dang dang nhap
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/hso?useSSL=false", "root",
                "");
                PreparedStatement pstmt = connection
                        .prepareStatement("SELECT tongnap,mocnap10 FROM account WHERE id = ?")) {
            pstmt.setString(1, currentUserId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                tongnap = rs.getInt("tongnap");
                mocnap10 = rs.getBoolean("mocnap10");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        double amount = Double.parseDouble(String.valueOf(tongnap));
        DecimalFormat formatter = new DecimalFormat("#,###");
        tongnapString = formatter.format(amount);
        // dieu kien nhan qua moc nap va chuyen qua cho player
        if (invent_able < 8) {
            Service.send_notice_box(conn, "kho đồ của bạn hiện không đủ vui lòng dọn trống 8 ô ");
        } else if (tongnap >= 15000000 && mocnap10 == false) {
            // mondo
            // item 7

            Item47 item5 = new Item47();
            item5.id = 381; // tâm linh 5
            item5.quantity = 20;
            conn.p.item.add_item_inventory47(7, item5);
            Item47 item6 = new Item47();
            item6.id = 361; // khải hoàn 5
            item6.quantity = 20;
            conn.p.item.add_item_inventory47(7, item6);
            Item47 item7 = new Item47();
            item7.id = 356; // hỗn nguyên 5
            item7.quantity = 20;
            conn.p.item.add_item_inventory47(7, item7);
            Item47 item3 = new Item47();
            item3.id = 45; // đục 3
            item3.quantity = 150;
            conn.p.item.add_item_inventory47(7, item3);
            Item47 item8 = new Item47();
            item8.id = 351; // đá krypton 3
            item8.quantity = 10;
            conn.p.item.add_item_inventory47(7, item8);
            Item47 item9 = new Item47();
            item9.id = 471; // đá hoả tinh
            item9.quantity = 15;
            conn.p.item.add_item_inventory47(7, item9);
            // item 3
            ItemTemplate3 temp3 = ItemTemplate3.item.get(4634);// mặt nạ loki
            Item3 it = new Item3();
            it.id = temp3.getId();
            it.name = temp3.getName();
            it.clazz = temp3.getClazz();
            it.type = temp3.getType();
            it.level = temp3.getLevel();
            it.icon = temp3.getIcon();
            it.op = temp3.getOp();
            it.color = temp3.getColor();
            it.islock = false;
            it.part = temp3.getPart();
            conn.p.item.add_item_inventory3(it);
            ItemTemplate3 temp4 = ItemTemplate3.item.get(4798);// giáp ki si rong
            Item3 it2 = new Item3();
            it2.id = temp4.getId();
            it2.name = temp4.getName();
            it2.clazz = temp4.getClazz();
            it2.type = temp4.getType();
            it2.level = temp4.getLevel();
            it2.icon = temp4.getIcon();
            it2.op = temp4.getOp();
            it2.color = temp4.getColor();
            it2.islock = false;
            it2.part = temp4.getPart();
            conn.p.item.add_item_inventory3(it2);

            ItemTemplate7 x1 = ItemTemplate7.item.get(381);
            ItemTemplate7 x2 = ItemTemplate7.item.get(361);
            ItemTemplate7 x3 = ItemTemplate7.item.get(356);
            ItemTemplate7 x4 = ItemTemplate7.item.get(45);
            ItemTemplate7 x5 = ItemTemplate7.item.get(351);
            ItemTemplate7 x6 = ItemTemplate7.item.get(471);
            int quant1_ = 20;
            int quant2_ = 20;
            int quant3_ = 20;
            int quant4_ = 150;
            int quant5_ = 10;
            int quant6_ = 15;

            Message m = new Message(78);
            m.writer().writeUTF("mốc 10 -15000k");
            m.writer().writeByte(8); // size
            // for (int i = 0; i < 3; i++) {
            m.writer().writeUTF(""); // name
            m.writer().writeShort(x1.getIcon()); // icon
            m.writer().writeInt(quant1_); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color
            //
            m.writer().writeUTF(""); // name
            m.writer().writeShort(x2.getIcon()); // icon
            m.writer().writeInt(quant2_); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color
            //
            m.writer().writeUTF(""); // name
            m.writer().writeShort(x3.getIcon()); // icon
            m.writer().writeInt(quant3_); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color

            m.writer().writeUTF(""); // name
            m.writer().writeShort(x4.getIcon()); // icon
            m.writer().writeInt(quant4_); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color

            m.writer().writeUTF(""); // name
            m.writer().writeShort(x5.getIcon()); // icon
            m.writer().writeInt(quant5_); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color

            m.writer().writeUTF(""); // name
            m.writer().writeShort(x6.getIcon()); // icon
            m.writer().writeInt(quant6_); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color

            m.writer().writeUTF("");
            m.writer().writeShort(temp3.getIcon());
            m.writer().writeInt(1);
            m.writer().writeByte(3);
            m.writer().writeByte(0);
            m.writer().writeByte(4);

            m.writer().writeUTF("");
            m.writer().writeShort(temp4.getIcon());
            m.writer().writeInt(1);
            m.writer().writeByte(3);
            m.writer().writeByte(0);
            m.writer().writeByte(4);

            m.writer().writeUTF("");
            m.writer().writeByte(1);
            m.writer().writeByte(0);
            conn.addmsg(m);
            m.cleanup();
            // update da nhan moc nap
            try (Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/hso?useSSL=false",
                    "root", "");
                    PreparedStatement pstmt = connection
                            .prepareStatement("update account set mocnap10=true WHERE id = ?")) {
                pstmt.setString(1, currentUserId);
                pstmt.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            // Service.send_notice_box(conn,"chúc mừng bạn đã hoàn thành mốc nạp 10 ->
            // 15,000,000");

        } else if (tongnap >= 15000000 && mocnap10 == true) {
            Service.send_notice_box(conn, "Bạn đã nhận mốc nạp này");
        } else if (tongnap < 15000000) {
            Service.send_notice_box(conn,
                    "tổng nạp của bạn chưa đủ mốc: 15,000,000 -> " + tongnapString + "/15,000,000");
        }
    }

    public static void quamocnap11(Session conn) throws IOException {
        String currentUserId = String.valueOf(conn.id); // Assuming you have a method to get the current user's ID
        Integer tongnap = null;
        Boolean mocnap11 = null;
        String tongnapString = null;
        int invent_able = conn.p.item.get_inventory_able();

        // lay tong nap va moc nap cua id dang dang nhap
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/hso?useSSL=false", "root",
                "");
                PreparedStatement pstmt = connection
                        .prepareStatement("SELECT tongnap,mocnap11 FROM account WHERE id = ?")) {
            pstmt.setString(1, currentUserId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                tongnap = rs.getInt("tongnap");
                mocnap11 = rs.getBoolean("mocnap11");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        double amount = Double.parseDouble(String.valueOf(tongnap));
        DecimalFormat formatter = new DecimalFormat("#,###");
        tongnapString = formatter.format(amount);

        // dieu kien nhan qua moc nap va chuyen qua cho player
        if (invent_able < 4) {
            Service.send_notice_box(conn, "kho đồ của bạn hiện không đủ vui lòng dọn trống 4 ô ");
        } else if (tongnap >= 17000000 && mocnap11 == false) {
            // mondo
            // item3
            ItemTemplate3 temp1 = ItemTemplate3.item.get(4759);// giap hellwing
            Item3 it = new Item3();
            it.id = temp1.getId();
            it.name = temp1.getName();
            it.clazz = temp1.getClazz();
            it.type = temp1.getType();
            it.level = temp1.getLevel();
            it.icon = temp1.getIcon();
            it.op = temp1.getOp();
            it.color = temp1.getColor();
            it.islock = false;
            it.part = temp1.getPart();
            conn.p.item.add_item_inventory3(it);
            ItemTemplate3 temp2 = ItemTemplate3.item.get(4799);// mặt nạ băng giá
            Item3 it2 = new Item3();
            it2.id = temp2.getId();
            it2.name = temp2.getName();
            it2.clazz = temp2.getClazz();
            it2.type = temp2.getType();
            it2.level = temp2.getLevel();
            it2.icon = temp2.getIcon();
            it2.op = temp2.getOp();
            it2.color = temp2.getColor();
            it2.islock = false;
            it2.part = temp2.getPart();
            conn.p.item.add_item_inventory3(it2);
            ItemTemplate3 temp3 = ItemTemplate3.item.get(4755);// tia sét 99
            Item3 it3 = new Item3();
            it3.id = temp3.getId();
            it3.name = temp3.getName();
            it3.clazz = temp3.getClazz();
            it3.type = temp3.getType();
            it3.level = temp3.getLevel();
            it3.icon = temp3.getIcon();
            it3.op = temp3.getOp();
            it3.color = temp3.getColor();
            it3.islock = false;
            it3.part = temp3.getPart();
            conn.p.item.add_item_inventory3(it3);
            ItemTemplate3 temp4 = ItemTemplate3.item.get(3269);// trứng đại bàng
            Item3 it4 = new Item3();
            it4.id = temp4.getId();
            it4.name = temp4.getName();
            it4.clazz = temp4.getClazz();
            it4.type = temp4.getType();
            it4.level = temp4.getLevel();
            it4.icon = temp4.getIcon();
            it4.op = temp4.getOp();
            it4.color = temp4.getColor();
            it4.islock = false;
            it4.part = temp4.getPart();
            conn.p.item.add_item_inventory3(it4);

            Message m = new Message(78);
            m.writer().writeUTF("mốc 11 -17000k");
            m.writer().writeByte(4); // size

            m.writer().writeUTF("");
            m.writer().writeShort(temp1.getIcon());
            m.writer().writeInt(1);
            m.writer().writeByte(3);
            m.writer().writeByte(0);
            m.writer().writeByte(4);

            m.writer().writeUTF("");
            m.writer().writeShort(temp2.getIcon());
            m.writer().writeInt(1);
            m.writer().writeByte(3);
            m.writer().writeByte(0);
            m.writer().writeByte(4);

            m.writer().writeUTF("");
            m.writer().writeShort(temp3.getIcon());
            m.writer().writeInt(1);
            m.writer().writeByte(3);
            m.writer().writeByte(0);
            m.writer().writeByte(4);

            m.writer().writeUTF("");
            m.writer().writeShort(temp4.getIcon());
            m.writer().writeInt(1);
            m.writer().writeByte(3);
            m.writer().writeByte(0);
            m.writer().writeByte(4);

            m.writer().writeUTF("");
            m.writer().writeByte(1);
            m.writer().writeByte(0);
            conn.addmsg(m);
            m.cleanup();

            // update da nhan moc nap
            try (Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/hso?useSSL=false",
                    "root", "");
                    PreparedStatement pstmt = connection
                            .prepareStatement("update account set mocnap11=true WHERE id = ?")) {
                pstmt.setString(1, currentUserId);
                pstmt.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            // Service.send_notice_box(conn,"chúc mừng bạn đã hoàn thành mốc nạp 11 ->
            // 17,000,000");

        } else if (tongnap >= 17000000 && mocnap11 == true) {
            Service.send_notice_box(conn, "Bạn đã nhận mốc nạp này");
        } else if (tongnap < 17000000) {
            Service.send_notice_box(conn,
                    "tổng nạp của bạn chưa đủ mốc: 17,000,000 -> " + tongnapString + "/17,000,000");
        }
    }

    public static void quamocnap12(Session conn) throws IOException {
        String currentUserId = String.valueOf(conn.id); // Assuming you have a method to get the current user's ID
        Integer tongnap = null;
        Boolean mocnap12 = null;
        String tongnapString = null;
        int invent_able = conn.p.item.get_inventory_able();

        // lay tong nap va moc nap cua id dang dang nhap
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/hso?useSSL=false", "root",
                "");
                PreparedStatement pstmt = connection
                        .prepareStatement("SELECT tongnap,mocnap12 FROM account WHERE id = ?")) {
            pstmt.setString(1, currentUserId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                tongnap = rs.getInt("tongnap");
                mocnap12 = rs.getBoolean("mocnap12");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        double amount = Double.parseDouble(String.valueOf(tongnap));
        DecimalFormat formatter = new DecimalFormat("#,###");
        tongnapString = formatter.format(amount);

        // dieu kien nhan qua moc nap va chuyen qua cho player
        if (invent_able < 5) {
            Service.send_notice_box(conn, "kho đồ của bạn hiện không đủ vui lòng dọn trống 5 ô ");
        } else if (tongnap >= 20000000 && mocnap12 == false) {
            // mondo
            Item47 item8 = new Item47();
            item8.id = 351; // đá krypton 2
            item8.quantity = 15;
            conn.p.item.add_item_inventory47(7, item8);
            Item47 item9 = new Item47();
            item9.id = 471; // đá hoả tinh
            item9.quantity = 20;
            conn.p.item.add_item_inventory47(7, item9);
            // item 3
            ItemTemplate3 temp1 = ItemTemplate3.item.get(4760);// giáp deathwing
            Item3 it = new Item3();
            it.id = temp1.getId();
            it.name = temp1.getName();
            it.clazz = temp1.getClazz();
            it.type = temp1.getType();
            it.level = temp1.getLevel();
            it.icon = temp1.getIcon();
            it.op = temp1.getOp();
            it.color = temp1.getColor();
            it.islock = false;
            it.part = temp1.getPart();
            conn.p.item.add_item_inventory3(it);
            ItemTemplate3 temp2 = ItemTemplate3.item.get(4617);// trứng rồng lửa
            Item3 it2 = new Item3();
            it2.id = temp2.getId();
            it2.name = temp2.getName();
            it2.clazz = temp2.getClazz();
            it2.type = temp2.getType();
            it2.level = temp2.getLevel();
            it2.icon = temp2.getIcon();
            it2.op = temp2.getOp();
            it2.color = temp2.getColor();
            it2.islock = false;
            it2.part = temp2.getPart();
            conn.p.item.add_item_inventory3(it2);
            ItemTemplate3 temp3 = ItemTemplate3.item.get(4690);// áo choàng 19
            Item3 it3 = new Item3();
            it3.id = temp3.getId();
            it3.name = temp3.getName();
            it3.clazz = temp3.getClazz();
            it3.type = temp3.getType();
            it3.level = temp3.getLevel();
            it3.icon = temp3.getIcon();
            it3.op = temp3.getOp();
            it3.color = temp3.getColor();
            it3.islock = false;
            it3.part = temp3.getPart();
            conn.p.item.add_item_inventory3(it3);

            ItemTemplate7 x1 = ItemTemplate7.item.get(351);
            ItemTemplate7 x2 = ItemTemplate7.item.get(471);

            int quant1_ = 15;
            int quant2_ = 20;

            Message m = new Message(78);
            m.writer().writeUTF("mốc 12 -20000k");
            m.writer().writeByte(5); // size
            // for (int i = 0; i < 3; i++) {
            m.writer().writeUTF(""); // name
            m.writer().writeShort(x1.getIcon()); // icon
            m.writer().writeInt(quant1_); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color
            //
            m.writer().writeUTF(""); // name
            m.writer().writeShort(x2.getIcon()); // icon
            m.writer().writeInt(quant2_); // quantity
            m.writer().writeByte(7); // type in bag
            m.writer().writeByte(0); // tier
            m.writer().writeByte(0); // color
            //

            m.writer().writeUTF("");
            m.writer().writeShort(temp1.getIcon());
            m.writer().writeInt(1);
            m.writer().writeByte(3);
            m.writer().writeByte(0);
            m.writer().writeByte(4);

            m.writer().writeUTF("");
            m.writer().writeShort(temp2.getIcon());
            m.writer().writeInt(1);
            m.writer().writeByte(3);
            m.writer().writeByte(0);
            m.writer().writeByte(4);

            m.writer().writeUTF("");
            m.writer().writeShort(temp3.getIcon());
            m.writer().writeInt(1);
            m.writer().writeByte(3);
            m.writer().writeByte(0);
            m.writer().writeByte(4);

            m.writer().writeUTF("");
            m.writer().writeByte(1);
            m.writer().writeByte(0);
            conn.addmsg(m);
            m.cleanup();
            // update da nhan moc nap
            try (Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/hso?useSSL=false",
                    "root", "");
                    PreparedStatement pstmt = connection
                            .prepareStatement("update account set mocnap12=true WHERE id = ?")) {
                pstmt.setString(1, currentUserId);
                pstmt.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            // Service.send_notice_box(conn,"chúc mừng bạn đã hoàn thành mốc nạp 12 ->
            // 20,000,000");

        } else if (tongnap >= 20000000 && mocnap12 == true) {
            Service.send_notice_box(conn, "Bạn đã nhận mốc nạp này");
        } else if (tongnap < 20000000) {
            Service.send_notice_box(conn,
                    "tổng nạp của bạn chưa đủ mốc: 20,000,000 -> " + tongnapString + "/20,000,000");
        }
    }
}
