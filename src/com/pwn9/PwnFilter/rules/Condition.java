/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter.rules;

import com.pwn9.PwnFilter.FilterState;

public class Condition {


    public enum CondFlag {
        NONE, ignore, require
    }

    public enum CondType {
        permission, user, string, command,
    }

    final CondType type;
    final CondFlag flag;
    final String parameters;


    public Condition(CondType t, CondFlag f, String p) {
        type = t;
        flag = f;
        parameters = p;
    }

    public static Condition newCondition(String line) {
        String[] parts = line.split("\\s",2);
        String conditionName = parts[0];
        String conditionData;
        conditionData = ((parts.length > 1) ? parts[1] : "");

        return newCondition(conditionName, conditionData);
    }

    public static Condition newCondition(String command, String parameterString) {
        String subCmd;
        CondType newType;
        CondFlag newFlag;
        String newParameters;

        try {
            newFlag = CondFlag.valueOf(command);
        } catch (IllegalArgumentException e) {
            return null;
        }

        String[] parts = parameterString.split("\\s", 2);
        subCmd = parts[0].toLowerCase();
        try {
            newType = Condition.CondType.valueOf(subCmd);
        } catch (IllegalArgumentException e) {
            return null;
        }
        if (parts.length > 1) {
            newParameters = parts[1];
        } else {
            newParameters = "";
        }

        return new Condition(newType, newFlag, newParameters);
    }

    public static boolean isCondition(String command) {
        try {
            return CondFlag.valueOf(command) != CondFlag.NONE;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Checks a message against this condition.  This method returns true if
     * the condition is met, false otherwise.  Processing of the current rule
     * will be aborted if _any_ check returns false.
     *
     * @param state The MessageState
     * @return true if this condition is met, false otherwise
     */
    public boolean check(FilterState state) {
        boolean matched = false;
        switch (type) {
            case user:
                for (String check : parameters.split("\\s")) {
                    if (state.playerName.equalsIgnoreCase(check)) matched = true;
                }
            case permission:
                for (String check: parameters.split("\\s")) {
                    if (state.playerHasPermission(check)) matched = true;
                }
            case string:
                for (String check: parameters.split("\\|")) {
                    if (state.getOriginalMessage().getPlainString().toUpperCase().contains(check.toUpperCase())) matched=true;
                }
            case command:
                for (String check: parameters.split("\\|")) {
                    if (state.getListenerName().equals("COMMAND")) {
                        String command = state.getOriginalMessage().getPlainString().split("\\s")[0].replaceFirst("^\\/","");
                        if (command.toUpperCase().matches(check.toUpperCase())) matched = true;
                    }
                }

        }
        switch (flag) {
            case ignore:
                return !matched;
            case require:
                return matched;
        }
        // Well, we shouldn't be able to get here, but in case we did, return false
        return false;
    }

}

