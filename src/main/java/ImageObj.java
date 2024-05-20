package main.java;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

public class ImageObj {
	
	private static Image businessManIcon;
	
	private static String businessManIconStr = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAAA7AAAAOwBeShxvQAAABl0RVh0U29mdHdhcmUAd3d3Lmlua3NjYXBlLm9yZ5vuPBoAAARzSURBVFiF7ZdLaFxVGMd/5z6TmWkmaaamWGsfFmmtjbRiLepCQaggRQqi4Ca4CsW9QnFTBV24UdwoBEKsgpSWLmIpgloXpbS1Qh8xJq1FTdLUvjJpMq/7OOdzkcyYVzO3zsKNf7hwH9/jd7/znXvOVSTU+HB/Lr5WOhtVKustQSnLmvc8jiJKUaCzHasvtq5rfjG79qWJJHFVEqMbp77ZXcxPHLcspQDEGCrlElEUIkZmAOKIMKgAkMpmw1Vr1j374HOvnGsYQE6caPq9PFKylKUAyqUCU/k8xuhl/VItK4otHdnchhferCxnZy33EGBM3TlVTV4qTjN553bd5AClqem0Dq3P6tnVBYjLpU4Ao2Om8omGtaby1NSehgGMMTZAqVBARO4LIChXsg0DKJlpkzCsYDsOLaty+KlUIgDRum58J1EkIJVtZcP2TrzmZgAK+TwT4+NM35mgOHkXo+f3hev7pFqzccMA6dUdxm9ps7LtGZjTfJm2NjJtbbVrHcXEUQAo3CYfy7IBig0DPPDoVoNSFuHdZe1s18F2Exe0prpjBKZuGe8tWZ46GYBM/nsANdI4gNEnFpnqCILpmeKYGMICmGixr8i3jQNIuB8RwZozvuE0REUo3Z45wsLMMV9lbPoaBlBtW/5AB19i+9SWDj/DPCDLBTezwFG9r9qfGKsbv55BVZK/+jMm3DGv1GJmoyx8DzlIrrNLKVX305mgCWdztD3yJBK+BfzTlMpamLwA8nbS5HAfFahKxs/lcPw3ULIbWA8olBpB+A7DQdWx7cb9xvxf/6nq9sChQ4c83/c/GN3+1OtuKm2vDUq1Jtz1+Rfr8u3tpeFXX74116c8OlaeHvrlhyAI9nd3dy/xhUoAcOzYsdVBEHxi2/ZepZR7tz3Hpe07eca1SQOZ0Wvs+uhTzPWbnOz5mDDbUvMd+/orwlIRpVTsed5xpdS+rq6ua0vlWTQNDx8+3Hn06NEzWutxx3FeU0q5IkLqr+usqpQZimfm/sM/noRKBSXCyouDNf9w5E9sMSilEBEnCII9QRCM9vb2nu7t7d22LMCRI0fe9TzvvG3bOwElIkRRRBiGaK1Zc+40kyLcMsLK4d+QSgDAygsDAEgcUx68RFNTE62traRSKSzLQkRUGIZPR1F0oa+v750lAfr7+ze7rvve3MRBEKC1ru0Fnck8D03c5koU4xeKMAvQfv4SiFAZGsSUy7Xgvu+TzWZJp9PViqgwDD/s6enZtAggjuMDzPZEHMdovfTWO3f2FFuGL6PCEGZ/SrzJKTKDQ1SuXlnSx/M80uk0AMYYZdv2gUUASqmNswb3TA5gxTGPnTkLC3bIemAAjLmnn+u6OE5tAVtcAcCuAtTTYC5DuCINvgfAzcc3M+7VdcO2bQBExF4EkHTxqOqnrZsguwLjeZzfujGRj1K1WV/LVauJ1vpXx3F2JAUYb2nm++d3IhZM2fXt58qyrIHaefWkUCh0a62viMiyP5NzlXcVk3byBdVxnIrjOJdFZF/13t9EufWQigKLQAAAAABJRU5ErkJggg==";

	// ## Getter ##
	
	public static Image getBusinessManIconIcon() {
		if(businessManIcon == null){
			businessManIcon = base64ToImg(businessManIconStr);
		}
		return businessManIcon;
	}
	
	// ## Main Icon ##
	public static Image getMainIcon() {
		// return getWinFolderIcon();
		return getBusinessManIconIcon();
	}
	
	// ## base64 To Image Process ##
	public static Image base64ToImg(String base64){
		
		Image img = null;
		
        String[] strings = base64.split(",");
        
        // String exec = strings[0].substring(11, 15);
        
        byte[] data = DatatypeConverter.parseBase64Binary(strings[1]);

		try {
			
			img = ImageIO.read(new ByteArrayInputStream(data));
			
		} catch (IOException err) {
			err.printStackTrace();
		}
		
		return img;
        
	}
	
}
