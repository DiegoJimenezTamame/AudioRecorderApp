package at.wifi.swdev.audiorecorderapp;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

public class WaveformVisualizerView extends View {

    private static final int MAX_AMPLITUDE = 32767; // Maximum amplitude value for 16-bit audio
    private Visualizer visualizer;
    private MediaPlayer mediaPlayer;
    private byte[] waveform;
    private Paint paint;

    public WaveformVisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setVisualizer(Visualizer visualizer) {
        if (visualizer != null) {
            this.visualizer = visualizer;
            visualizer.setEnabled(true);
            setupVisualizer();
        }
    }
    private void init() {
        paint = new Paint();
        paint.setColor(0xFF47F9FE);
        paint.setStrokeWidth(2f);
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
        setupVisualizer();
    }

    private void setupVisualizer() {
        if (mediaPlayer != null) {
            visualizer = new Visualizer(mediaPlayer.getAudioSessionId());
            // Set capture size to a relatively large value for better resolution
            visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
            // Set scaling mode to capture higher range of amplitudes
            visualizer.setScalingMode(Visualizer.SCALING_MODE_NORMALIZED);
            // Set smoothing to get smoother waveform visualization
            visualizer.setScalingMode(Visualizer.SCALING_MODE_AS_PLAYED);
            visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
                @Override
                public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
                    WaveformVisualizerView.this.waveform = waveform;
                    invalidate(); // Redraw the view when new waveform data is available
                }

                @Override
                public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
                    // Ignore FFT data
                }
            }, Visualizer.getMaxCaptureRate() / 2, true, false);

            visualizer.setEnabled(true);
        } else {
            release();
        }
    }
    // Method to update waveform data
    public void setWaveform(byte[] waveform) {
        this.waveform = waveform;
        invalidate(); // Trigger redraw
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        if (waveform != null) {
            float width = getWidth();
            float height = getHeight();
            float centerY = height / 2f;

            float increment = width / (waveform.length - 1);
            float x = 0;

            for (int i = 0; i < waveform.length - 1; i++) {
                float startY = centerY + (waveform[i] / (float) MAX_AMPLITUDE) * centerY;
                float endY = centerY + (waveform[i + 1] / (float) MAX_AMPLITUDE) * centerY;

                // Define gradient colors
                int startColor = 0xFF47F9FE; // Cyan color
                int endColor = 0xFFFF0000; // Red color

                // Create gradient shader
                Shader shader = new LinearGradient(x, startY, x + increment, endY, startColor, endColor, Shader.TileMode.CLAMP);
                paint.setShader(shader);

                // Draw line segment
                canvas.drawLine(x, startY, x + increment, endY, paint);

                x += increment;
            }
        }
    }

    public void release() {
        if (visualizer != null) {
            visualizer.release();
            visualizer = null;
        }
    }
}
