package com.example.kidsdrawingapp

import android.content.Context
import android.graphics.*
import android.nfc.Tag
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val TAG = "DrawingView"
    private lateinit var mDrawPath: CustomPath
    private var mCanvasBitmap: Bitmap? = null
    private lateinit var  mDrawPaint: Paint
    private var mCanvasPaint: Paint? = null
    private var mBrushSize: Float = 0F
    private var color = Color.BLACK
    private var canvas: Canvas? = null
    private val mPaths = ArrayList<CustomPath>()

    init{
        setupDrawing()
    }

    private fun setupDrawing() {
        mDrawPaint = Paint() // take care of style
        mDrawPath  = CustomPath(color, mBrushSize) // take care of draw path
        mDrawPaint.color = color
        mDrawPaint.style = Paint.Style.STROKE
        mDrawPaint.strokeJoin = Paint.Join.ROUND
        mDrawPaint.strokeCap = Paint.Cap.ROUND

        mCanvasPaint = Paint(Paint.DITHER_FLAG)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(mCanvasBitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(mCanvasBitmap!!, 0f, 0f, mCanvasPaint)
        Log.d(TAG, "mBrushsize = ${mDrawPath.brushThickness}")

        /*to keep the draw permanent redraw all*/
        for(p in mPaths){
            mDrawPaint.strokeWidth = p.brushThickness
            mDrawPaint.color = p.color
            canvas.drawPath(p, mDrawPaint)
        }

        /*to see the current draw in each instant*/
        if(!mDrawPath.isEmpty){
            mDrawPaint.strokeWidth = mDrawPath.brushThickness
            mDrawPaint.color = mDrawPath.color
            canvas.drawPath(mDrawPath, mDrawPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x
        val touchY = event?.y

        when(event?.action){
            MotionEvent.ACTION_DOWN -> {
                mDrawPath.color = color
                mDrawPath.brushThickness = mBrushSize
                //mDrawPath.reset() //clear
                //Log.d(TAG, "mBrushsize = ${mDrawPath!!.brushThickness}")
                if (touchX != null && touchY != null) {
                    mDrawPath.moveTo(touchX, touchY)
                }//set to the point (x,y)
            }

            MotionEvent.ACTION_MOVE -> {
                if (touchX != null && touchY != null) {
                    mDrawPath.lineTo(touchX, touchY)
                }
            }

            MotionEvent.ACTION_UP -> {
                mPaths.add(mDrawPath)
                mDrawPath = CustomPath(color, mBrushSize)
            }
            else -> return false
        }

        invalidate()
        return true
    }

    fun setSizeForBrush(newSize: Float){
        mBrushSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            newSize,
            resources.displayMetrics
        )
        mDrawPaint.strokeWidth = mBrushSize
    }

    fun setColor(newColor: String){
        color = Color.parseColor(newColor)
        mDrawPaint!!.color = color
    }

    internal inner class CustomPath(
        var color: Int,
        var brushThickness: Float
    ): Path() {

    }

}