package com.example.yacupmobilea.tracker;

public enum DetectorSignal {
    SILENCE {
        @Override
        public String getReadableName() {
            return "...";
        }
    },
    BREATH_IN {
        @Override
        public String getReadableName() {
            return "in ";
        }
    },
    BREATH_OUT {
        @Override
        public String getReadableName() {
            return "out";
        }
    };

    public abstract String getReadableName();
}
