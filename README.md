## ConditionalEditText (CEDT) ##

A extension to standard EditText that lets you specify a condition and checks for it each time the text is changed. Each time a text change takes place:

* If no condition is specified, it is highlighted by a blue border.
* If condition is specified and not met, it is highlighted by a red border and
    * If condition description has been set, it gets shown as a small popdown from the CEDT.
* If condition is specified and met, the popdown is dismissed and the border turns green.

### Usage ###

#### XML ####
Since CEDT extends EditText you can use it just as you would use an EditText. It provides an additional attribute `condition_description` to describe the condition to the user ("Password must be atleast 6 characters" etc). The equivalent Java method is `setConditionDescription(String)`

To use this attribute you need to add `xmlns:custom="http://schemas.android.com/apk/res-auto"` to the layout tag.

#### Java ####
Set a ConditionalEditTextListener to your CEDT using `setConditionalEditTextListener(ConditionalEditTextListener)`. This interface defines three methods

* `boolean condition(View, String)`
* `onConditionSatisfied(View, String)`
* `onConditionUnsatisfied(View, String)`

You define the condition and actions to be taken on condition changes using these methods.

#### Example ####
    public class MyActivity extends Activity implements ConditionalEditTextListener
    {
        ConditionalEditText cedt;
        Button btn;

        public void onCreate(Bundle savedInstanceState)
        {
            ....
            cedt = (ConditionalEditTextListener)findViewById(R.id.cedt);
            btn = (Button)findViewById(R.id.btn);
            cedt.setConditionalEditTextListener(this);
            // These can be done via XML too
            cedt.setConditionDescription("Must be atleast 6 characters.");
            btn.setEnabled(false);
        }

        public boolean condition(View edt, String s)
        {
            return (s.length() >= 6);
        }

        public void onConditionSatisfied(View edt, String s)
        {
             btn.setEnabled(true);
        }

        public void onConditionUnsatisfied(View edt, String s)
        {
             btn.setEnabled(false);
        }
    }   

