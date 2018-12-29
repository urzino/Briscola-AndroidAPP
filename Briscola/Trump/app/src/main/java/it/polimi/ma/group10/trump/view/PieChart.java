package it.polimi.ma.group10.trump.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import it.polimi.ma.group10.trump.R;


/**
 * This class is used to draw the PieChart which displays the user statistics
 */
public class PieChart extends View {

    private final String font = "fonts/bangers.ttf";
    private final int[] legendLabels = {R.string.wins,R.string.draws,R.string.losses};
    private final int[] colors = {
            getResources().getColor(R.color.dark_green),
            getResources().getColor(R.color.dark_orange),
            getResources().getColor(R.color.dark_red),
            getResources().getColor(R.color.light_green),
            getResources().getColor(R.color.light_orange),
            getResources().getColor(R.color.light_red)};

    private int wins=0;
    private float winsPerc=0;
    private int losses=0;
    private float lossesPerc=0;
    private int draws=0;
    private float drawsPerc=0;
    private int total=0;

    public PieChart(Context context) {
        super(context);
    }

    public PieChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PieChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PieChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setPercentage(int wins,int losses,int draws){
        this.total = wins + losses + draws;
        this.wins = wins;
        this.losses = losses;
        this.draws = draws;
        this.winsPerc = Math.round(((float)wins/(float)total)*10000)/10000f;
        this.lossesPerc = Math.round(((float)losses/(float)total)*10000)/10000f;
        this.drawsPerc = Math.round(((float)draws/(float)total)*10000)/10000f;

        drawableStateChanged();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPieChart(canvas);
        drawLegend(canvas);
    }

    /**
     * This method draws the PieChart using the percentage of wins, draws, losses to determine the dimension of each portion of the chart
     * @param canvas
     */
    private void drawPieChart(Canvas canvas){
        //calculate the center of the PieChart
        Paint paint = new Paint();
        int rectDim = getWidth()/4-getWidth()/64;
        int radiusDim = rectDim/2;
        float centerX = getX() + getWidth()/2 - getWidth()/4;
        float centerY = getY() + getHeight()/2 ;
        RectF rect = new RectF(centerX-rectDim,centerY-rectDim,centerX+rectDim,centerY+rectDim);

        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(1);
        //draws the portion of graph related to the wins
        if(winsPerc!= 0) {
            paint.setShader(new LinearGradient(0, 0, 0, getHeight(), colors[3], colors[0], Shader.TileMode.CLAMP));
            canvas.drawArc(rect, 0, winsPerc * 360, true, paint);
        }
        //draws the portion of graph related to the draws
        if(drawsPerc!=0) {
            paint.setShader(new LinearGradient(0, 0, 0, getHeight(), colors[4], colors[1], Shader.TileMode.CLAMP));
            canvas.drawArc(rect, winsPerc * 360, drawsPerc * 360, true, paint);
        }
        //draws the portion of graph related to the losses
        if(lossesPerc!=0) {
            paint.setShader(new LinearGradient(0, 0, 0, getHeight(), colors[5], colors[2], Shader.TileMode.CLAMP));
            canvas.drawArc(rect, winsPerc * 360 + drawsPerc * 360, lossesPerc * 360, true, paint);
        }

        //draws the white circle around the violet center
        paint.setShader(null);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(centerX,centerY,radiusDim + 10,paint);
        //draws the center of the PieChart
        paint.setShader(new LinearGradient(0, 0, 0, getHeight(), getResources().getColor(R.color.dark_violet), getResources().getColor(R.color.light_violet), Shader.TileMode.CLAMP));
        canvas.drawCircle(centerX,centerY,radiusDim,paint);
        //draws the total number of games inside the violet circle
        paint.setColor(Color.WHITE);
        paint.setShader(null);
        String text = Integer.toString(total);
        paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,getResources().getDimension(R.dimen.stats_legend_size),getResources().getDisplayMetrics()));
        Rect bounds = new Rect();
        paint.getTextBounds(text,0,text.length(),bounds);
        int x = (int)centerX - bounds.centerX();
        int y = (int)centerY - bounds.centerY();
        canvas.drawText(text,x,y,paint);

    }

    /**
     * This method draws all the items of the legend and sets the correct width and height of the legend
     * @param canvas
     */
    private void drawLegend(Canvas canvas){
        //set the right position of the legend
        int rectDim=getWidth()/4;
        float centerX = getX() + getWidth()/2 + getWidth()/4;
        float centerY = getY() + getHeight()/2;

        Paint legendPaint = new Paint();

        RectF legend = new RectF(centerX-rectDim,centerY-rectDim,centerX+rectDim,centerY+rectDim);
        legendPaint.setColor(Color.TRANSPARENT);
        canvas.drawRect(legend,legendPaint);

        float y = legend.top - legend.height()/6;
        float x = legend.left + legend.width()/8;
        //draw the legend for wins,draws and losses
        for(int i = 0;i<legendLabels.length;i++){
            y += (legend.height()/3);
            drawLegendText(canvas,i,(int)(x + 2*rectDim/10f ),(int)y);
            drawSquare(canvas,rectDim,i,(int)x,(int)y);
        }

    }

    /**
     * This method draws the text associated to each item of the legend
     * @param canvas
     * @param label
     * @param x
     * @param y
     */
    private void drawLegendText(Canvas canvas,int label,int x, int y){
        Paint paint = new Paint();
        Rect bounds = new Rect();
        String text = getContext().getString(legendLabels[label]);
        String value,perc;

        switch (label){
            case 0 : {
                value = Integer.toString(wins);
                perc = String.format("%.2f",winsPerc*100);
                break;
            }
            case 1 : {
                value = Integer.toString(draws);
                perc = String.format("%.2f",drawsPerc*100);
                break;
            }
            case 2 : {
                value = Integer.toString(losses);
                perc = String.format("%.2f",lossesPerc*100);
                break;
            }
            default: {
                value="";
                perc="";
            }
        }

        text += " ("+value+") "+ perc +"%";

        paint.setTypeface(Typeface.createFromAsset(getContext().getAssets(), font));
        paint.setColor(Color.WHITE);
        paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,getResources().getDimension(R.dimen.stats_legend_size),getResources().getDisplayMetrics()));
        paint.getTextBounds(text,0,text.length(),bounds);
        canvas.drawText(text,x,y-bounds.centerY(),paint);
    }

    /**
     * This method draws the square used in the legend to indicate the color of the associate legend's item
     * @param canvas
     * @param dimension
     * @param color
     * @param x
     * @param y
     */
    private void drawSquare(Canvas canvas,int dimension,int color,int x,int y){
        int dim = dimension/10;
        Paint paint = new Paint();
        paint.setShader(new LinearGradient(0, 0, 0,getHeight(), colors[color], colors[color+3], Shader.TileMode.CLAMP));
        Rect rect = new Rect(x-dim,y-dim,x+dim,y+dim);
        canvas.drawRect(rect,paint);
    }



}
