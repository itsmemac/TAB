package me.neznamy.tab.shared.backend;

import org.jetbrains.annotations.NotNull;

public interface EntityData {

    /** Marker flag in armor stand flags */
    byte MARKER_FLAG = 1 << 4;

    static int getArmorStandFlagsPosition(int minorVersion) {
        if (minorVersion >= 17) {
            //1.17.x, 1.18.x, 1.19.x, 1.20.x
            return 15;
        } else if (minorVersion >= 15) {
            //1.15.x, 1.16.x
            return 14;
        } else if (minorVersion == 14) {
            //1.14.x
            return 13;
        } else if (minorVersion >= 10) {
            //1.10.x - 1.13.x
            return 11;
        } else {
            //1.8.x - 1.9.x
            return 10;
        }
    }
    
    @NotNull Object build();
}
