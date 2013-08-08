package net.thetranquilpsychonaut.ConditionalEditText;

import android.view.View;

public interface ConditionalEditTextListener
{

    public boolean condition( View edt, String s );

    public void onConditionSatisfied( View edt, String s );

    public void onConditionUnsatisfied( View edt, String s );
}
