package Game.map;

import java.util.List;

import Game.template.Option;

public class ItemMap {
    public long time_exist;
    public byte color;
    public byte category;
    public short idmaster;
    public long time_pick;
    public short id_item;
    public int quantity;
    public List<Option> op;
    public byte type;

    public ItemMap(byte type) {
        this.type = type;
    }
}
