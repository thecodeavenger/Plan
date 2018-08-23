/*
 * License is provided in the jar as LICENSE also here:
 * https://github.com/Rsl1122/Plan-PlayerAnalytics/blob/master/Plan/src/main/resources/LICENSE
 */
package com.djrapitops.plan.system.tasks;

import com.djrapitops.plan.Plan;
import com.djrapitops.plan.system.settings.config.PlanConfig;
import com.djrapitops.plan.system.tasks.server.*;
import com.djrapitops.plugin.api.Check;
import com.djrapitops.plugin.task.RunnableFactory;
import org.bukkit.Bukkit;

import javax.inject.Inject;

/**
 * TaskSystem responsible for registering tasks for Bukkit.
 *
 * @author Rsl1122
 */
public class BukkitTaskSystem extends ServerTaskSystem {

    private final Plan plugin;

    @Inject
    public BukkitTaskSystem(
            Plan plugin,
            PlanConfig config,
            RunnableFactory runnableFactory,
            PaperTPSCountTimer paperTPSCountTimer,
            BukkitTPSCountTimer bukkitTPSCountTimer,
            NetworkPageRefreshTask networkPageRefreshTask,
            BootAnalysisTask bootAnalysisTask,
            PeriodicAnalysisTask periodicAnalysisTask
    ) {
        super(
                runnableFactory,
                Check.isPaperAvailable()
                        ? paperTPSCountTimer
                        : bukkitTPSCountTimer,
                config,
                networkPageRefreshTask,
                bootAnalysisTask,
                periodicAnalysisTask
        );
        this.plugin = plugin;
    }

    @Override
    public void enable() {
        super.enable();
        try {
            PingCountTimer pingCountTimer = new PingCountTimer(runnableFactory);

            plugin.registerListener(pingCountTimer);
            registerTask("PingCountTimer", pingCountTimer)
                    .runTaskTimer(20L, PingCountTimer.PING_INTERVAL);
        } catch (ExceptionInInitializerError | NoClassDefFoundError ignore) {
            // Running CraftBukkit
        }
    }

    @Override
    public void disable() {
        super.disable();
        Bukkit.getScheduler().cancelTasks(plugin);
    }
}
