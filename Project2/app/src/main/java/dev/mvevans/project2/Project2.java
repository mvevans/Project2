package dev.mvevans.project2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.os.Bundle;
import java.lang.Math;
import android.view.Menu;
import android.view.MenuItem;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;



public class Project2 extends Activity {
    enum functions {Xsq, Sin2PiX, Two, Sin2Theta}

    SGL gl;
    ByteBuffer buffer;
    IntBuffer vbuffer;
    IntBuffer cbuffer;
    ByteBuffer ibuffer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Part 1
        //GraphView gv = new GraphView(this);
        //setContentView(gv);

        //Part 2
        gl = new SGL(this);
        setContentView(gl);

    }

    //Part 1 **************************************
    class GraphView extends View {
        int width;
        int height;
        double xcenter;
        double ycenter;
        float radius;


        private Paint paint = new Paint();

        public GraphView(Context context) {
            super(context);
        }

        protected void onDraw(Canvas canvas) {
            width = canvas.getWidth();
            height = canvas.getHeight();
            xcenter = width / 2.0;
            ycenter = height / 2.0;
            radius = (float) ((1 / 10.5) * Math.min(width, height) / 2.0);
            int xres = 1000;

            canvas.drawColor(Color.WHITE); // clear background
            paint.setStyle(Paint.Style.STROKE);
            paint.setAlpha(255);
            paint.setColor(Color.BLUE);
            canvas.drawLine(0, 0, 200, 200, paint);
            canvas.drawCircle(200, 200, 100, paint);
            paint.setTypeface(Typeface.MONOSPACE);
            paint.setTextSize(10);
            canvas.drawText("abcdef", 100, 100, paint);
            paint.setTextSize(20);
            canvas.drawText("abcdef", 100, 200, paint);

            //draw axis
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(3);
            canvas.drawLine(horz(-10.0), vert(0.0), horz(10.0), vert(0.0), paint);
            canvas.drawLine(horz(0.0), vert(-10.0), horz(0.0), vert(10.0), paint);
            canvas.drawText("Y", horz(0.5), vert(10), paint);
            canvas.drawText("X", horz(10), vert(-0.5), paint);
            for (int i = -10; i <= 10; i++) {
                canvas.drawLine(horz(-0.1), vert(i), horz(0.1), vert(i), paint);
                canvas.drawLine(horz(i), vert(-0.1), horz(i), vert(0.1), paint);

            }

            drawFunction(canvas, -2, 2, xres, functions.Xsq, Color.BLUE);
            drawFunction(canvas, 0, 1, xres, functions.Sin2PiX, Color.RED);
            drawPolarFunction(canvas, 0, 2.0 * Math.PI, xres, functions.Two, Color.GREEN);
            drawPolarFunction(canvas, 0, 2.0 * Math.PI, xres, functions.Sin2Theta, Color.CYAN);
            /*
            * Modify this code to draw coordinate axes, label the axes “x” and “y,” and plot the following mathematical
            *functions:
            *To convert polar to rectangular use x=rsin θ and y=r cosθ .
            *y = x^2        (−2<x<2).
            *y = sin 2πx (0<x<1).
            *r = 2 (polar, 0<θ<2π ).
            *r = sin 2θ ( polar, 0<θ<2π )
            * */


        }

        private void drawPolarFunction(Canvas canvas, double thetaStart, double thetaEnd, double thetaRes, functions f, int col) {
            paint.setColor(col);
            double len = thetaEnd - thetaStart;
            double thetaInterval = len / thetaRes;
            for (double i = thetaStart; i < thetaEnd; i += thetaInterval) {
                double r = 1.0;
                switch (f) {//cheap easy workaround to doing callbacks
                    case Sin2Theta:
                        r = sinTwoThetaFunction(i);
                        break;
                    case Two:
                        r = twoFunction();
                        break;
                    default:
                        r = 1.0;
                        break;
                }
                float nx = horz(r * Math.sin(i));
                float ny = vert(r * Math.cos(i));
                canvas.drawPoint(nx, ny, paint);
            }
        }

        private void drawFunction(Canvas canvas, double xStart, double xEnd, double xRes, functions f, int col) {
            paint.setColor(col);
            double len = xEnd - xStart;
            double iv = len / xRes;
            for (int i = 0; i < xRes; i++) {
                double ix = (xStart + (iv * i));
                float nx = horz(ix);
                float ny = 0;
                switch (f) {//cheap easy workaround to doing callbacks
                    case Xsq:
                        ny = vert(xSqFunction(ix));
                        break;
                    case Sin2PiX:
                        ny = vert(sin2XPIFunction(ix));
                        break;
                    default:
                        ny = nx;
                        break;
                }
                canvas.drawPoint(nx, ny, paint);
            }
        }

        private double twoFunction() {
            return 2.0;
        }

        private double sinTwoThetaFunction(double theta) {
            return Math.sin(2.0 * theta);
        }

        private double xSqFunction(double x) {
            return Math.pow(x, 2);
        }

        private double sin2XPIFunction(double x) {
            return Math.sin((double) 2.0 * Math.PI * x);
        }


        float horz(double x) {
            return (float) (xcenter + x * radius);
        }

        float vert(double y) {
            return (float) (ycenter - y * radius);
        }

    }

    //Part 2*******************************************
    class SGL extends GLSurfaceView implements Renderer
    {
        public SGL(Context context)
        {
            super(context);
            setRenderer(this);
            int one = 0x10000;
            int half = 0x04000;
            int zero = 0x00000;
            int [] vertices = new int[4*3];
            for (int i=0; i<2; i++)
                for (int j=0; j<2; j++)
                {
                    vertices[2*3*i + 3*j] = i*0x10000;
                    vertices[2*3*i + 3*j + 1] = j*0x10000;
                    vertices[2*3*i + 3*j + 2] = 0;
                }int [] colors = new int[4*4];
            for (int i=0; i<4; i++)
            {
                colors[i*4] = 0x10000;
                colors[i*4+1] =0x00000;
                colors[i*4+2] = 0x00000;
                colors[i*4+3] = 0x10000;
            }
            byte [] indices = new byte[6];
            indices[0] = (byte)0; indices[1] = (byte)1;
            indices[2] = (byte)0; indices[3] = (byte)2;
            indices[4] = (byte)2; indices[5] = (byte)3;

            buffer = ByteBuffer.allocateDirect(4*4*3);
            buffer.order(ByteOrder.nativeOrder());
            vbuffer = buffer.asIntBuffer();
            vbuffer.put(vertices);
            vbuffer.position(0);
            buffer = ByteBuffer.allocateDirect(4*4*4);
            buffer.order(ByteOrder.nativeOrder());
            cbuffer = buffer.asIntBuffer();
            cbuffer.put(colors);
            cbuffer.position(0);
            ibuffer = ByteBuffer.allocateDirect(6);
            ibuffer.put (indices);
            ibuffer.position(0);
        }

        public void onDrawFrame (GL10 gl)
        {
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadIdentity();
            gl.glTranslatef(0, 0, -2.5f);
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
            gl.glVertexPointer(3, GL10.GL_FIXED, 0, vbuffer);
            gl.glColorPointer(4, GL10.GL_FIXED, 0, cbuffer);
            gl.glDrawElements(GL10.GL_LINES, 6, GL10.GL_UNSIGNED_BYTE, ibuffer);
        }
        public void onSurfaceChanged(GL10 gl, int width, int height)
        {
            gl.glViewport(0, 0, width, height);
            float ratio = (float) width / height;
            gl.glMatrixMode(GL10.GL_PROJECTION);
            gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
        }
        public void onSurfaceCreated(GL10 gl, EGLConfig config)
        {
            gl.glClearColor(1,1,1,1);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.project2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
