package com.daalzzwi.kidalkidal.config;

import android.content.Context;

public class ConfigContext {

    private static Context context;

    public static Context getContext() { return context; }

    public static void setContext( Context context ) { ConfigContext.context = context; }
}
