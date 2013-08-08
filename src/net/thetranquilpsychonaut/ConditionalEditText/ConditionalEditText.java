package net.thetranquilpsychonaut.ConditionalEditText;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * An extension to standard EditText that checks the entered text
 * against the specified condition each time it is modified
 */
public class ConditionalEditText extends EditText implements TextWatcher
{
    private Paint paint;
    private ConditionalEditTextListener cedtl;
    private boolean conditionSatisfied;
    private PopupTextView ptv;
    int cBlue=Color.rgb( 34, 34, 223 );
    int cRed=Color.rgb( 223, 34, 34 );
    int cGreen=Color.rgb( 34, 139, 34 );
    int cGray=Color.GRAY;

    public ConditionalEditText( Context context )
    {
        super( context );
        init( context, null, 0 );
    }

    public ConditionalEditText( Context context, AttributeSet attrs )
    {
        super( context, attrs );
        init( context, attrs, 0 );
    }

    public ConditionalEditText( Context context, AttributeSet attrs, int defStyle )
    {
        super( context, attrs, defStyle );
        init( context, attrs, defStyle );
    }

    private void init( Context context, AttributeSet attrs, int defStyle )
    {
        paint=new Paint();
        paint.setStyle( Paint.Style.STROKE );
        paint.setStrokeWidth( 4f );
        paint.setAntiAlias( true );
        paint.setHinting( Paint.HINTING_ON );
        this.addTextChangedListener( this );
        // Obtain the description for the condition specified in XML
        TypedArray a=context.obtainStyledAttributes( attrs, R.styleable.ConditionalEditText );
        int N=a.getIndexCount();
        for ( int i=0; i < N; ++i )
        {
            int j=a.getIndex( i );
            if ( j == R.styleable.ConditionalEditText_condition_description )
            {
                ptv=new PopupTextView( this );
                ptv.setText( a.getString( i ) );
            }
        }
        a.recycle();
    }

    public void setConditionalEditTextListener( ConditionalEditTextListener cedtl )
    {
        this.cedtl=cedtl;
    }

    public boolean isConditionSatisfied()
    {
        return conditionSatisfied;
    }

    public void setConditionDescription( String conditionDescription )
    {
        // If ptv was not initialized by setting the description in XMl, we do so here
        if ( ptv == null )
            ptv=new PopupTextView( this );
        ptv.setText( conditionDescription );
    }

    @Override
    protected void onFocusChanged( boolean focused, int direction, Rect previouslyFocusedRect )
    {
        super.onFocusChanged( focused, direction, previouslyFocusedRect );
        // If ptv is null, do nothing
        if ( ptv == null )
            return;
        // If ptv is not null and if we lose focus, then dismiss ptv
        if ( !focused )
            ptv.dismiss();
            // else if we are gaining focus...
        else
        {
            // and if condition is false, then show ptv
            if ( !conditionSatisfied )
                ptv.showLikePopDownMenu();
        }
    }

    @Override
    protected void onDraw( Canvas canvas )
    {
        super.onDraw( canvas );
        // Override all default style
        setBackgroundColor( 0 );
        boolean focused=isFocused();
        boolean isEmpty=getText().toString().isEmpty();
        if ( focused )
        {
            if ( cedtl == null )
                paint.setColor( cBlue );
            else if ( conditionSatisfied )
                paint.setColor( cGreen );
            else
                paint.setColor( cRed );
        }
        else
        {
            if ( isEmpty || conditionSatisfied )
                paint.setColor( cGray );
            else
                paint.setColor( cRed );
        }
        canvas.drawRect( 0, 0, getWidth(), getHeight(), paint );
    }

    @Override
    public void beforeTextChanged( CharSequence s, int start, int count, int after )
    {
    }

    @Override
    public void onTextChanged( CharSequence s, int start, int before, int count )
    {
    }

    @Override
    public void afterTextChanged( Editable s )
    {
        // If no listener is set, do nothing
        if ( this.cedtl == null )
            return;

        // If listener is set and condition is true
        if ( this.cedtl.condition( this, s.toString() ) )
        {
            // And if condition was not true before this change, call onConditionSatisfied
            if ( !conditionSatisfied )
                this.cedtl.onConditionSatisfied( this, s.toString() );
            this.conditionSatisfied=true;
            // Check for null to avoid NPE
            if ( ptv != null )
                ptv.dismiss();
        }
        // If listener is set and condition is false
        else
        {
            // And if condition was true before this change, call onConditionUnsatisfied
            if ( conditionSatisfied )
                this.cedtl.onConditionUnsatisfied( this, s.toString() );
            this.conditionSatisfied=false;
            // Check for null to avoid NPE
            if ( ptv != null )
                ptv.showLikePopDownMenu();
        }
        // Force onDraw after each text change to repaint the border
        this.invalidate();
    }

    /**
     * A small popup that contains a single textview
     */
    static class PopupTextView extends BetterPopupWindow
    {
        LinearLayout ll;
        TextView tv;

        public PopupTextView( View anchor )
        {
            super( anchor );
            this.setBackgroundDrawable( this.anchor.getResources().getDrawable( R.drawable.popupbg ) );
        }

        @Override
        protected void onCreate()
        {
            ll=new LinearLayout( this.anchor.getContext() );
            tv=new TextView( ll.getContext() );
            ll.addView( tv );
            this.setContentView( ll );
        }

        public void setText( String s )
        {
            this.tv.setText( s );
        }
    }
}
