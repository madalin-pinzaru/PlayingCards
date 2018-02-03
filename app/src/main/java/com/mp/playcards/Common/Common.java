package com.mp.playcards.Common;

import com.mp.playcards.Model.User;

/**
 * Created by Madalin on 17-Ian-18.
 */

public class Common {
    public static User currentUser;

    public static String convertCodeToStatus(String status) {
        if(status.equals("0"))
            return "Order Placed";
        else if(status.equals("1"))
            return "In Transit";
        else
            return "Received";
    }
}
