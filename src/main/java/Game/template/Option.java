package Game.template;

import Game.Helps.CheckItem;

public class Option {

    private static final int[] parafterupdate = new int[]{1, 110, 120, 130, 140, 150, 160, 172, 184, 196, 208, 220, 235, 250, 265, 280};
    public byte id;
    public int param;
    private short idItem;

    public Option(int id, int param, short iditem) {
        this.id = (byte) id;
        if (id == -115 || id == -103 || id == -88 || id == -87) {
            this.param = 180_000;
        } else {
            this.param = param;
        }
        this.idItem = iditem;
    }

    public Option(int id, int param) {
        this.id = (byte) id;
        this.param = param;
    }

    public int getParam(int tier) {
        if ((id >= 100 && id <= 107) || (id >= 58 && id <= 60)) {
            return param;
        }
        if (CheckItem.isArmor(idItem)) {
            return param;
        }
        if (CheckItem.isMeDay(idItem)) {
            return getParamMD(tier);
        }
        if (CheckItem.isWing(idItem)) {
            return getParamWing(tier);
        }
        //return param;
        if (tier == 0) {
            return param;
        }
        //
        int parbuffer = this.param;

        if (this.id >= 29 && this.id <= 36 || this.id >= 16 && this.id <= 22 || this.id == 41) {
            parbuffer += 20 * tier;
            return parbuffer;
        }

        if (this.id >= 23 && this.id <= 26) {
            return (parbuffer + tier);
        }
        if (this.id == 42) {
            return (parbuffer + tier * 400);
        }
        if ((this.id >= 7 && this.id <= 13) || this.id == 15 || this.id == 27 || this.id == 28) {
            return (parbuffer + 100 * tier);
        }
        if ((this.id == 37 || this.id == 38)) {
            return tier >= 9 ? 2 : 1;
        }
        if (tier > 15) {
            tier = 15;
        }
        if ((this.id >= 0 && this.id <= 6) || this.id == 14 || this.id == 40) {
            parbuffer = (parafterupdate[tier] * this.param) / 100;
            return parbuffer;
        }
        return parbuffer;
    }

    public int getParamMD(int tier) {
        if (tier == 0) {
            return param;
        }
        if ((this.id == 37 || this.id == 38)) {
            return 1;
        }
        //
        int parbuffer = this.param;
        if (this.id == 81 || this.id == 86 || this.id == 88 || this.id == 77 || this.id == 79) // giây dòng vip
        {
            return (int) (parbuffer * tier * 0.3);
        }
        if (this.id == 85 || this.id == 87 || this.id == 80 || this.id == 82) // dòng vip
        {
            return (int) (parbuffer * tier * 0.031);
        }
        if (this.id == 78 || this.id == 76) // dòng vip
        {
            return (int) (parbuffer * tier * 0.1);
        }

        if ((this.id >= 76 && this.id <= 89) || this.id == 97 || this.id == 98 || this.id == 95) // dòng vip
        {
            return (int) (parbuffer * tier * 0.07);
        }
        if (this.id >= 29 && this.id <= 36 || this.id >= 16 && this.id <= 22 || this.id == 41) {
            parbuffer += 20 * tier;
            return parbuffer;
        }

        if (this.id >= 23 && this.id <= 26) {
            return (parbuffer + tier);
        }
        if (this.id == 42) {
            return (parbuffer + tier * 400);
        }
        if ((this.id >= 7 && this.id <= 13)) {
            return (int) (this.param * (tier + 1.5));
        }
        if (this.id == 15 || this.id == 27 || this.id == 28) {
            return (parbuffer + 100 * tier);
        }
        if (tier > 15) {
            tier = 15;
        }
        if (this.id == 14) {
            parbuffer = (parafterupdate[tier] * this.param) / 100;
            return parbuffer;
        }
        if (this.id <= 6) {
            if (tier == 1 || tier == 2) {
                parbuffer = (int) (this.param + this.param  * (0.3 * tier));
            } else if (tier == 3 || tier == 4) {
                parbuffer = (int) (this.param * (tier - 0.99));
            } else {
                parbuffer = (int) (this.param * (tier + 0.1 - 6) + this.param * (tier + 0.1));
            }
            if (tier == 15) {
                parbuffer += (int) (this.param * 1.1);
            }
            return parbuffer;
        }
        return parbuffer;
    }
    public int getParamWing(int tier) {
        try {
            if (tier == 0) {
                return param;
            }
            if ((this.id == 37 || this.id == 38)) {
                return 1;
            }
            int parbuffer = this.param;
            if (this.id >= 29 && this.id <= 36) {
                return (parbuffer + 20 * (tier - 25));
            }
            if (this.id >= 23 && this.id <= 26) {
                return (parbuffer + tier);
            }
            if (this.id == 27 || this.id == 28) {
                return (parbuffer + 100 * (tier - 15));
            }
            if (this.id == 41) {
                return (parbuffer + tier * 20);
            }
            if (this.id == 42) {
                return (parbuffer + tier * 400);
            }
            if ((this.id >= 7 && this.id <= 11)) {
                return parbuffer + 100 * (tier - 10);
            }
            if (this.id == 15) {
                return parbuffer + 20 * (tier - 20);
            }
            if (this.id == 14) {
                int par = 0;
                if (tier > 15) {
                    par = parafterupdate[15] + (tier -15) * 15;
                }
                parbuffer = (par * this.param) / 100;
                return parbuffer;
            }
            return parbuffer;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    public void setParam(int param) {
        this.param = param;
    }
}
