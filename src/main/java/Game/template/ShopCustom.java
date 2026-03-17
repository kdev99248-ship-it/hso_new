package Game.template;

import Game.client.Player;
import Game.core.Admin;
import Game.core.Service;

import java.io.IOException;
import java.util.HashMap;

public class ShopCustom {
    public static final HashMap<Integer, Long> items_gems = new HashMap<>();

    public static void setShopGems() {
        items_gems.put(4587, 100000L);
        items_gems.put(4588, 100000L);
        items_gems.put(4589, 100000L);
        items_gems.put(4590, 100000L);
        items_gems.put(4632, 500000L);
        items_gems.put(4633, 500000L);
        items_gems.put(4634, 1000000L);
        items_gems.put(4635, 1000000L);
        items_gems.put(4636, 1000000L);
        items_gems.put(4716, 500000L);
        items_gems.put(4717, 500000L);
        items_gems.put(4718, 700000L);
        items_gems.put(4719, 500000L);
        items_gems.put(4799, 1200000L);
        items_gems.put(4690, 1500000L);
        for (int i = 4720; i < 4728; i++) {
            items_gems.put(i, 1200000L);
        }
        for (int i = 4766; i < 4768; i++) {
            items_gems.put(i, 1200000L);
        }
        for (int i = 4775; i < 4784; i++) {
            items_gems.put(i, 1200000L);
        }
        items_gems.put(4802, 1200000L);
        items_gems.put(4759, 1500000L);
        items_gems.put(4760, 1500000L);

        items_gems.put(4617, 1500000L);
        items_gems.put(4626, 1500000L);
        items_gems.put(3269, 1500000L);
        items_gems.put(4631, 1000000L);
    }
    public static void buy(Player p, int idbuy) throws IOException {
        if (p.get_ngoc() < items_gems.get(idbuy)) {
            Service.send_notice_box(p.conn, "Không đủ ngọc");
            return;
        }
        p.update_ngoc(-items_gems.get(idbuy), "trừ %s ngọc từ mua item cửa hàng");
        if (ItemTemplate3.item.get(idbuy).getType() == 16) {
            Admin.randomMedal(p, (byte) 4, (byte) 0, true);
        } else {
            Item3 itbag = new Item3();
            itbag.id = (short) idbuy;
            itbag.clazz = ItemTemplate3.item.get(idbuy).getClazz();
            itbag.type = ItemTemplate3.item.get(idbuy).getType();
            itbag.level = ItemTemplate3.item.get(idbuy).getLevel();
            itbag.icon = ItemTemplate3.item.get(idbuy).getIcon();
            itbag.color = 5;
            itbag.part = ItemTemplate3.item.get(idbuy).getPart();
            itbag.islock = true;
            itbag.name = ItemTemplate3.item.get(idbuy).getName();
            itbag.tier = 0;
            itbag.op = ItemTemplate3.item.get(idbuy).getOp();
            itbag.time_use = 0;
            p.item.add_item_inventory3(itbag);
        }
        Service.send_notice_box(p.conn, "Đã mua thành công");
    }
}
