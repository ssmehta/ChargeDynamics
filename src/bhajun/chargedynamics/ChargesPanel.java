package bhajun.chargedynamics;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import bhajun.chargedynamics.Ball;

import java.util.ArrayList;
import java.util.List;

public class ChargesPanel extends SurfaceView implements Runnable, SurfaceHolder.Callback {
	private Thread game;
	private boolean isRunning;
	private List<Ball> charges;
	
	private boolean chargeChange;
	
	public ChargesPanel(Context context) {
		super(context);
		getHolder().addCallback(this);
		setFocusable(true);
		
		game = new Thread(this);
		charges = new ArrayList<Ball>();
		
		chargeChange = false;
	}
	
	public boolean onTouchEvent(MotionEvent e) {
		if (e.getAction() == MotionEvent.ACTION_DOWN) {
			boolean removedCharge = false;
			
			for(Ball b : charges) {
				if(e.getX() >= (b.getX() - b.getWidth() / 2) && (e.getX() <= (b.getX() + b.getWidth() / 2)) &&
						e.getY() >= (b.getY() - b.getHeight() / 2) && (e.getY() <= (b.getY() + b.getHeight() / 2))) {
					charges.remove(b);
					removedCharge = true;
					break;
				}
			}
			
			if(!removedCharge)
				charges.add(new Ball(BitmapFactory.decodeResource(getResources(), R.drawable.ball), e.getX(), e.getY()));
			
			chargeChange = true;
			return true;
		}
		
		return false;
	}
	
	public void render(Canvas canvas) {
		canvas.drawColor(Color.BLACK);
		
		for(Ball b : charges)
			b.draw(canvas);
	}
	
	public void update() {
		for(int i = 0; i < charges.size(); i++) {
			for(int j = i + 1; j < charges.size(); j++) {
				// Find the nearest object image due to periodic boundary conditions
				double dx = charges.get(i).getX() - charges.get(j).getX();
				double dy = charges.get(i).getY() - charges.get(j).getY();
				dx -= getWidth() * Math.round(dx / getWidth());
				dy -= getHeight() * Math.round(dy / getHeight());
				
				double d = Math.pow(dx * dx + dy * dy, 0.5);
				double q = charges.get(i).getCharge() * charges.get(j).getCharge();
				
				charges.get(i).updateForce(q * dx / (d * d * d), q * dy / (d * d * d));
				charges.get(j).updateForce(-q * dx / (d * d * d), -q * dy / (d * d * d));
				
				if(chargeChange)
					break;
			}
			
			if(chargeChange)
				break;
		}
		
		for(Ball b : charges) {
			if(chargeChange)
				b.resetForce();
			else
				b.updatePos(this);
		}
		
		chargeChange = false;
	}
	
	public void run() {
		SurfaceHolder surfaceHolder = getHolder();
		Canvas canvas;
		
		while(isRunning) {
			canvas = null;
			
			try {
				canvas = surfaceHolder.lockCanvas();
				synchronized(surfaceHolder) {
					update();
					render(canvas);
				}
				
				Thread.sleep(10);
			} catch(Exception e) {} 
			finally {
				if (canvas != null)
					surfaceHolder.unlockCanvasAndPost(canvas);
			}
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

	public void surfaceCreated(SurfaceHolder holder) {
		isRunning = true;
		game.start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		isRunning = false;
		boolean isRetrying = true;
		
		while(isRetrying) {
			try {
				game.join();
				isRetrying = false;
			} catch (InterruptedException e) {}
		}
	}
}
