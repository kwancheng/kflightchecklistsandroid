package com.acproma.kflightchecklists.utils;

import android.util.Log;

import statemap.FSMContext;
import statemap.State;

/**
 * Created by Congee on 9/16/14.
 */
abstract public class KfclStateMachine extends FSMContext {
    protected KfclStateMachine(State initState) {
        super(initState);
    }
}
