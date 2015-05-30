/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2014 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter.rules.action;

import com.pwn9.PwnFilter.FilterState;
import com.pwn9.PwnFilter.PwnFilter;
import com.pwn9.PwnFilter.util.FileUtil;
import com.pwn9.PwnFilter.util.LogManager;
import com.pwn9.PwnFilter.util.Patterns;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import java.io.*;
import java.util.ArrayList;

/**
 * Broadcasts the contents of the named file to all users.
 */
public class Actionbroadcastfile implements Action {
    ArrayList<String> messageStrings = new ArrayList<String>();

    public void init(String s)
    {
        File textDir = PwnFilter.getInstance().getTextDir();
        if (textDir == null) return; // Er... Probably should return something, or at least log...

        File textfile = FileUtil.getFile(textDir,s,false);
        if (textfile == null) return;

        try {
            FileInputStream fs  = new FileInputStream(textfile);
            BufferedReader br = new BufferedReader(new InputStreamReader(fs));
            String message;
            while ( (message = br.readLine()) != null ) {
                messageStrings.add(ChatColor.translateAlternateColorCodes('&',message));
            }
        } catch (FileNotFoundException ex) {
            LogManager.logger.warning("File not found while trying to add Action: " + ex.getMessage());
        } catch (IOException ex) {
            LogManager.logger.warning("Error reading: " + textfile.getName());
        }
    }

    public boolean execute(final FilterState state ) {
        final ArrayList<String> preparedMessages = new ArrayList<String>();

        for (String message : messageStrings) {
            preparedMessages.add(Patterns.replaceVars(message,state));
        }

        state.addLogMessage("Broadcasted: "+preparedMessages.get(0) + (preparedMessages.size()>1?"...":""));

        Bukkit.getScheduler().runTask(state.plugin, new Runnable() {
            @Override
            public void run() {
                for (String m : preparedMessages) {
                    Bukkit.broadcastMessage(m);
                }
            }
        });

        return true;
    }
}

