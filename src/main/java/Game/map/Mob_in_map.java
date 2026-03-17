package Game.map;

import java.util.ArrayList;
import java.util.List;

import Game.Boss.BossServer;
import Game.client.Player;
import Game.core.Manager;
import Game.core.Service;
import Game.core.Util;
import Game.activities.ChiemThanhManager;
import Game.template.MainObject;
import Game.io.Message;

import java.io.IOException;
import java.util.HashMap;

public class Mob_in_map extends MainObject {
    public final static HashMap<Integer, Mob_in_map> ENTRY = new HashMap<>();
    public int time_refresh = 8;
    public boolean is_boss;
    public long time_back;
    public final List<Player> list_fight = new ArrayList<>();
    public long time_fight;
    public boolean is_boss_active;
    public int timeBossRecive = 1000 * 60 * 60 * 8;
    public int count_meterial = 50;
    public final HashMap<String, Long> top_dame = new HashMap<>();

    public void Reset() {
        hp = get_HpMax();
        isdie = false;
        synchronized (list_fight) {
            list_fight.clear();
        }
        synchronized (top_dame) {
            top_dame.clear();
        }
    }

    public void Set_isBoss(boolean isBoss) {
        is_boss = isBoss;
    }

    @Override
    public boolean isBoss() {
        return is_boss;
    }

    @Override
    public boolean isHouse() {
        return template.mob_id >= 89 && template.mob_id <= 92;
    }

    @Override
    public boolean isMob() {
        return true;
    }

    @Override
    public int get_DameBase() {
        if (dame <= 0) {
            dame = level * 30;//dame quai
        }

        int dmob = Util.random((int) (this.dame * 0.95), (int) (this.dame * 1.05));
        if (this.level > 30 && this.level <= 50) {
            dmob = (dmob * 13) / 10;
        } else if (this.level > 50 && this.level <= 70) {
            dmob = (dmob * 16) / 10;
        } else if (this.level > 70 && this.level <= 100) {
            dmob = (dmob * 19) / 10;
        } else if (this.level > 100 && this.level <= 600) {
            dmob = (dmob * 21) / 10;
        }
        if (this.is_boss) {
            dmob = (int) (dmob * this.level * 0.004);
        }
        if (this.color_name != 0 && (this.template.mob_id < 89 || this.template.mob_id > 92)) {
            dmob *= 2;
        }
        return dmob;
    }

    @Override
    public int get_Miss(boolean giam_ne) {
        return 800;
    }

    @Override
    public void SetDie(Map map, MainObject mainAtk) throws IOException {
        if (isdie) return;
        hp = 0;
        isdie = true;

        Mob_in_map mob = this;
        if (mainAtk.isPlayer()) {
            if ((mainAtk).hieuchien > 0 && Math.abs(mainAtk.level - mob.level) <= 5) {
                (mainAtk).hieuchien--;
            }
            if (mob.template.mob_id == 152) {
                ChiemThanhManager.SetOwner((Player) mainAtk);
            }
        }
        Player p = (Player) mainAtk;
        if (mainAtk.isPlayer() && p.isOwner && !p.isSquire && Math.abs(mob.level - p.level) < 10) {
            int point_activity = 10 + mob.level - p.level;
            p.point_activity += point_activity;
        }
        if (mainAtk.isPlayer() && p.quest_daily != null && p.quest_daily[0] != -1 && p.quest_daily[2] < p.quest_daily[3]
                && p.quest_daily[0] == this.template.mob_id) {
            p.quest_daily[2] += 1;
            Service.send_notice_nobox_white(p.conn, "Nhiệm vụ hàng ngày " + p.quest_daily[2] + "/" + p.quest_daily[3]);
        }
        p.checkQuest(this.template.mob_id, (byte) 1);
        boolean check_mob_roi_ngoc_kham = mob.template.mob_id >= 167 && mob.template.mob_id <= 172;
        if (mob.isBoss()) {
            if (!map.isMapChienTruong()) {
                map.BossDie(mob);
                long time_refresh = System.currentTimeMillis() + 14400000L;
                BossServer.setTimeRefresh(map.map_id, map.zone_id, time_refresh);
                String p_name = "";
                long top_dame = 0;
                for (java.util.Map.Entry<String, Long> en : mob.top_dame.entrySet()) {
                    if (en.getValue() > top_dame) {
                        top_dame = en.getValue();
                        p_name = en.getKey();
                    }
                }
                mob.isdie = true;
                if (!Map.is_map_cant_save_site(mob.map_id)) {
                    Manager.gI().chatKTGprocess(mainAtk.name + " đã tiêu diệt " + mob.template.name);
                    Manager.gI().chatKTGprocess(p_name + " đã nhận quà Top 1 sát thương đánh " + mob.template.name);
                    Player player_top_dame = Map.get_player_by_name(p_name);
                    if (player_top_dame != null) {
                        LeaveItemMap.leave_item_boss(map, mob, player_top_dame);
                        LeaveItemMap.leave_item_3(map, mob, player_top_dame);
                    }
                }
                if (mainAtk.isPlayer()) {
                    LeaveItemMap.leave_item_boss(map, mob, (Player) mainAtk);
                    LeaveItemMap.leave_item_3(map, mob, (Player) mainAtk);
                }
            } else {
                Manager.gI().chatKTGprocess(mainAtk.name + " đã tiêu diệt " + mob.template.name);
                ((Player) mainAtk).update_point_arena(50);
                mob.isdie = true;
                if (mainAtk.isPlayer()) {
                    LeaveItemMap.leave_item_boss(map, mob, (Player) mainAtk);
                    LeaveItemMap.leave_item_3(map, mob, (Player) mainAtk);
                }
            }
        } else {
            mob.time_back = System.currentTimeMillis() + (mob.time_refresh * 1000L) - 1000L;
            if (mainAtk.isPlayer()) {
                if (Math.abs(mob.level - mainAtk.level) <= 10 && !check_mob_roi_ngoc_kham) {
//                    if (Math.abs(mob.level - mainAtk.level) <= 5 && Manager.gI().event == 3 && Util.random_ratio(10)) {
//                        event.Event_3.LeaveItemMap(map, this, mainAtk);
//                    } else if (mob.level >= 133) {
//                        event.Event_3.LeaveItemMap(map, this, mainAtk);
//                    }
                    if (map.isMapLangPhuSuong()) {
                        int percent = 20;
                        if (percent > Util.random(0, 300)) {
                            LeaveItemMap.leave_vang(map, mob, (Player) mainAtk);
                        } else if (percent > Util.random(0, 300)) {
                            LeaveItemMap.leave_item_by_type7(map, (short) Util.random(481, 485), p, mob.ID);
                        } else if (percent > Util.random(0, 300)) {
                            LeaveItemMap.leave_item_by_type7(map, (short) Util.random(472, 480), p, mob.ID);
                        } else if (percent > Util.random(0, 100)) {
                            LeaveItemMap.leave_item_by_type7(map, (short) Util.random(0, 2), p, mob.ID);
                        }
                        if (Manager.gI().event != -1 && 30 > Util.random(0, 100) && Math.abs(mob.level - mainAtk.level) <= 5) {
                            LeaveItemMap.leave_item_event(map, mob, (Player) mainAtk);
                        }
                    } else {
                        int percent = 20;
                        if (zone_id == 1 && !Map.is_map_not_zone2(map_id)
                                && p.get_EffDefault(-127) != null) {
                            percent = 40;
                        }
                        if (percent > Util.random(0, 300)) {
                            LeaveItemMap.leave_item_3(map, mob, (Player) mainAtk);
                        } else if (percent > Util.random(0, 300) && zone_id == 1 && !Map.is_map_not_zone2(map_id)
                                && p.get_EffDefault(-127) != null) {
                            if (Util.random(0, 10) < 2) {
                                LeaveItemMap.leave_item_by_type7(map, (short) Util.random(116, 126), p, mob.ID);
                            } else {
                                LeaveItemMap.leave_item_by_type7(map, (short) 13, p, mob.ID);
                            }
                        } else if (percent > Util.random(0, 200)) {
                            LeaveItemMap.leave_material(map, mob, (Player) mainAtk);
                        } else if (percent > Util.random(0, 100)) {
                            LeaveItemMap.leave_item_7(map, mob, (Player) mainAtk);
                        } else if (percent > Util.random(0, 100)) {
                            LeaveItemMap.leave_vang(map, mob, (Player) mainAtk);
                        }
                        if (percent > Util.random(0, 300)) {
                            LeaveItemMap.leave_item_4(map, mob, (Player) mainAtk);
                        }
                        if (Manager.gI().event != -1 && 30 > Util.random(0, 100) && Math.abs(mob.level - mainAtk.level) <= 5) {
                            LeaveItemMap.leave_item_event(map, mob, (Player) mainAtk);
                        }
                    }
                }
                if (check_mob_roi_ngoc_kham) {
                    LeaveItemMap.leave_material_ngockham(map, mob, (Player) mainAtk);
                    if (Util.random(0, 10) < 3) {
                        short nltt = ((short) (Util.random(10) * 4 + Util.random(419, 421)));
                        LeaveItemMap.leave_item_by_type7(map, nltt, (Player) mainAtk, mob.ID);
                    }
                }
                if (mob.color_name != 0) {
                    map.num_mob_super--;
                }
            }
        }
        dropNgocXanh(map, (Player) mainAtk, mob.ID);
    }

    private void dropNgocXanh(Map map, Player mainAtk, int id) throws IOException {
        int percent = Util.random(20, 50);
        if (Util.random(1, 100) > percent) {
            int kcQuantity = Math.max(id * percent / 200, 1);
            mainAtk.update_ngoc(kcQuantity, "");
            Service.send_notice_nobox_white(mainAtk.conn, "Bạn nhận được " + kcQuantity + " Kim Cương");
        }
    }

    @Override
    public void update(Map map) {
        try {
            if (this.isdie && this.ishs && this.time_back < System.currentTimeMillis() && !is_boss && !this.isHouse()) {
                this.isdie = false;
                this.Reset();
                this.hp = this.get_HpMax();
                if (this.isBoss()) {
                    this.color_name = 3;
                } else if (5 > Util.random(200) && map.num_mob_super < 2 && this.level > 50) {
                    this.color_name = (new byte[]{1, 2, 4, 5})[Util.random(4)];
                    map.num_mob_super++;
                } else {
                    this.color_name = 0;
                }
                for (int j = 0; j < map.players.size(); j++) {
                    Player pp = map.players.get(j);
                    if ((Math.abs(pp.x - this.x) < 200) && (Math.abs(pp.y - this.y) < 200)) {
                        if (!pp.other_mob_inside.containsKey(this.ID)) {
                            pp.other_mob_inside.put(this.ID, true);
                        }
                        if (pp.other_mob_inside.get(this.ID)) {
                            Message mm = new Message(4);
                            mm.writer().writeByte(1);
                            mm.writer().writeShort(this.template.mob_id);
                            mm.writer().writeShort(this.ID);
                            mm.writer().writeShort(this.x);
                            mm.writer().writeShort(this.y);
                            mm.writer().writeByte(-1);
                            pp.conn.addmsg(mm);
                            mm.cleanup();
                            pp.other_mob_inside.replace(this.ID, true, false);
                        } else {
                            Service.mob_in4(pp, this.ID);
                        }
                    }
                }
            } else if (!this.isdie && this.isATK && this.time_fight < System.currentTimeMillis()) {
                if ((this.template.mob_id == 151 || this.template.mob_id == 152 || this.template.mob_id == 154)) {
                    for (Player p0 : this.list_fight) {
                        if (p0 != null && !p0.isdie && p0.map.map_id == this.map_id && p0.map.zone_id == this.zone_id
                                && Math.abs(this.x - p0.x) < 200 && Math.abs(this.y - p0.y) < 200) {
                            MainObject.MainAttack(map, this, p0, 0, null, 2);
                        }
                    }
                    this.time_fight = System.currentTimeMillis() + 3500L;
                } else if (!this.list_fight.isEmpty()) {
                    Player p0 = this.list_fight.get(Util.random(this.list_fight.size()));
                    if (p0 != null && !p0.isdie && p0.map.map_id == this.map_id && p0.map.zone_id == this.zone_id) {
                        if (Math.abs(this.x - p0.x) < 200 && Math.abs(this.y - p0.y) < 200) {
                            if (this.time_fight < System.currentTimeMillis()) {
                                this.time_fight = System.currentTimeMillis() + 1200L;
                                MainObject.MainAttack(map, this, p0, 0, null, 2);
                            }
                        } else {
                            this.list_fight.remove(p0);
                            //
                            Message m = new Message(10);
                            m.writer().writeByte(0);
                            m.writer().writeShort(this.ID);
                            MapService.send_msg_player_inside(map, p0, m, true);
                            m.cleanup();
                        }
                    }
                    if (p0.isdie) {
                        this.list_fight.remove(p0);
                        //
                        Message m = new Message(10);
                        m.writer().writeByte(0);
                        m.writer().writeShort(this.ID);
                        MapService.send_msg_player_inside(map, p0, m, true);
                        m.cleanup();
                    }
                    if (this.list_fight.contains(p0) && !(p0.map.map_id == this.map_id && p0.map.zone_id == this.zone_id)) {
                        this.list_fight.remove(p0);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public short getIdMaterial() {
        if (this.template.mob_id == 103) {
            return 136;
        } else if (this.template.mob_id == 104) {
            return 137;
        } else if (this.template.mob_id == 101) {
            return 138;
        } else if (this.template.mob_id == 84) {
            return 139;
        } else if (this.template.mob_id == 105) {
            return 140;
        } else if (this.template.mob_id == 83) {
            return 141;
        } else if (this.template.mob_id == 106) {
            return (short) Util.random(142, 144);
        } else if (this.template.mob_id == 149) {
            return (short) Util.random(145, 147);
        } else if (this.template.mob_id == 155) {
            return (short) Util.random(136, 146);
        }
        return -1;
    }
}
