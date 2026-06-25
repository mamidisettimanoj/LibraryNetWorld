package com.librarynet.dsa.optimization;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class ActivitySelection {
    public static final class Activity {
        private final String name;
        private final int start;
        private final int finish;

        public Activity(String name, int start, int finish) {
            if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Activity name cannot be empty");
            if (start < 0 || finish < start) throw new IllegalArgumentException("Invalid activity time");
            this.name = name.trim();
            this.start = start;
            this.finish = finish;
        }

        public String getName() { return name; }
        public int getStart() { return start; }
        public int getFinish() { return finish; }
        @Override public String toString() { return name + "[" + start + "," + finish + "]"; }
    }

    private ActivitySelection() { }

    public static List<Activity> selectMaximumCompatible(List<Activity> activities) {
        List<Activity> sorted = new ArrayList<>(activities);
        sorted.sort(Comparator.comparingInt(Activity::getFinish).thenComparingInt(Activity::getStart));
        List<Activity> selected = new ArrayList<>();
        int lastFinish = -1;
        for (Activity activity : sorted) {
            if (activity.getStart() >= lastFinish) {
                selected.add(activity);
                lastFinish = activity.getFinish();
            }
        }
        return selected;
    }
}
