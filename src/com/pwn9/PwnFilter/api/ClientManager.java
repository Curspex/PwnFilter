

/*
 * PwnFilter -- Regex-based User Filter Plugin for Bukkit-based Minecraft servers.
 * Copyright (c) 2013 Pwn9.com. Tremor77 <admin@pwn9.com> & Sage905 <patrick@toal.ca>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package com.pwn9.PwnFilter.api;

import com.pwn9.PwnFilter.PwnFilter;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handle Startup / Shutdown / Configuration of our PwnFilter Clients
 * User: ptoal
 * Date: 13-09-29
 * Time: 9:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class ClientManager {

    private static ClientManager _instance;

    private ConcurrentHashMap<FilterClient,Plugin> registeredClients = new ConcurrentHashMap<FilterClient, Plugin>();

    private final PwnFilter plugin;

    private ClientManager(PwnFilter plugin) {
        this.plugin = plugin;
    }

    public static ClientManager getInstance() {
        if (_instance == null ) {
            _instance = new ClientManager(PwnFilter.getInstance());
        }
        return _instance;
    }

    public List<FilterClient> getActiveClients() {
        List<FilterClient> result = new ArrayList<FilterClient>();
        for (FilterClient f : registeredClients.keySet()) {
            if (f.isActive()) result.add(f);
        }
        return result;
    }

    public Map<FilterClient,Plugin> getRegisteredClients() {
        return registeredClients;
    }

    public void enableClients() {
        Configuration config = plugin.getConfig();

        for (FilterClient f : registeredClients.keySet()) {
            f.activate(config);
        }

    }

    public void disableClients() {
        for (FilterClient f : getActiveClients()) {
            f.shutdown();
        }
    }

    /**
     * Add a listener to the PwnFilter ListenerManager.  This allows PwnFilter
     * to notify the listener when it should try to activate or shutdown.
     * PwnFilter will call the activate / shutdown methods when reloading
     * rules configs.
     *
     * The FilterListener must register *before* attempting to use any other
     * PwnFilter resources.
     *
     * @param f FilterListener instance
     * @param p Plugin that the listener belongs to.
     * @return True if the listener was added, false if it was already registered.
     */
    public boolean registerClient(FilterClient f, Plugin p) {
        if (registeredClients.containsKey(f)) {
            return false; // Already Registered
        }
        registeredClients.put(f, p);
        return true;
    }

    /**
     * Remove a listener from the PwnFilter ListenerManager.
     * The listener will no longer be activated / deactivated when PwnFilter
     * reloads configs, rules, etc.
     * IMPORTANT: Before de-registering, the FilterListener must remove all
     * references to RuleSets.
     *
     * @param f FilterListener to remove.
     * @return true if the listener was previously registered and successfully
     * removed. False if it was not registered.
     */
    public boolean unregisterClient(FilterClient f) {
        if (registeredClients.containsKey(f)) {
            registeredClients.remove(f);
            return true;
        } else {
            return false;
        }
    }

    public void unregisterClients() {
        for (FilterClient f : registeredClients.keySet() ) {
            f.shutdown();
            registeredClients.remove(f);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        disableClients();
    }

}
