package bhajun.chargedynamics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

public class Ball {
	public static int PARTICLE_SPEED = 2;
	
	private Bitmap bitmap;
	private double charge;
	private float x, y;
	private double f_x, f_y;
	
	public Ball(Bitmap bitmap, float x, float y) {
		this.bitmap = bitmap;
		this.x = x;
		this.y = y;
		this.charge = 1;
		
		resetForce();
	}
	
	public void updateForce(double df_x, double df_y) {
		f_x += df_x;
		f_y += df_y;
	}
	
	public void resetForce() {
		f_x = 0.0;
		f_y = 0.0;
	}
	
	public void updatePos(View v) {
		double f = Math.pow(f_x * f_x + f_y * f_y, 0.5);
		if(f > 0) {
			f_x /= f;
			f_y /= f;
		}
		
		//if(PARTICLE_SPEED * f_x >= 1.0e-6)
			x += PARTICLE_SPEED * f_x;
		//if(PARTICLE_SPEED * f_y >= 1.0e-6)
			y += PARTICLE_SPEED * f_y;
		
		resetForce();
		
		// Display periodic boundary conditions
		if(x <= 0)
			x += v.getWidth();
		else if(x >= v.getWidth())
			x -= v.getWidth();
		
		if(y <= 0)
			y += v.getHeight();
		else if(y >= v.getHeight())
			y -= v.getHeight(); 
	}
	
	public void draw(Canvas canvas) {
		// Draw the ball at its real position
		canvas.drawBitmap(bitmap, x - (getWidth() / 2), y - (getHeight() / 2), null);
		
		// Fix partial-images due to periodic boundary conditions
		if(x - (getWidth() / 2) <= 0)
			canvas.drawBitmap(bitmap, x - (getWidth() / 2) + canvas.getWidth(), y - (getHeight() / 2), null);
		else if(x + (getWidth() / 2) >= canvas.getWidth())
			canvas.drawBitmap(bitmap, x - (getWidth() / 2) - canvas.getWidth(), y - (getHeight() / 2), null);
		
		if(y - (getHeight() / 2) <= 0)
			canvas.drawBitmap(bitmap, x - (getWidth() / 2), y - (getHeight() / 2) + canvas.getHeight(), null);
		else if(y + (getHeight() / 2) >= canvas.getHeight())
			canvas.drawBitmap(bitmap, x - (getWidth() / 2), y - (getHeight() / 2) - canvas.getHeight(), null);
		
	}
	
	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	
	public int getWidth() {
		return getBitmap().getWidth();
	}
	
	public int getHeight() {
		return getBitmap().getHeight();
	}
		
	public double getCharge() {
		return charge;
	}
	
	public void setCharge(double charge) {
		this.charge = charge;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}
}