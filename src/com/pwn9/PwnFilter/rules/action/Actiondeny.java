/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter.rules.action;

import com.pwn9.PwnFilter.FilterState;

/**
 * Deny this event by cancelling it.
 */
public class Actiondeny implements Action {

    public void init(String s)
    {
        // Do nothing with a string, if one is provided.
    }

    public boolean execute(final FilterState state ) {
        state.cancel = true;
        return true;
    }
}
