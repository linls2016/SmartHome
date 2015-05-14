package com.lins.smarthome;

import java.util.concurrent.LinkedBlockingQueue;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import edu.cmu.pocketsphinx.Config;
import edu.cmu.pocketsphinx.Decoder;
import edu.cmu.pocketsphinx.Hypothesis;

public class RecognizerTask implements Runnable {

	private Handler mainHandler;
	private int picNum = 1;

	private Decoder ps;
	private AudioTask audio;
	private Thread audio_thread;
	private LinkedBlockingQueue<short[]> audioq;
	private RecognitionListener rl;
	private boolean use_partials;
	
	private Event mailbox;

	class AudioTask implements Runnable {
		LinkedBlockingQueue<short[]> q;
		AudioRecord rec;
		int block_size;
		boolean done;

		static final int DEFAULT_BLOCK_SIZE = 512;

		AudioTask() {
			this.init(new LinkedBlockingQueue<short[]>(), DEFAULT_BLOCK_SIZE);
		}

		AudioTask(LinkedBlockingQueue<short[]> q) {
			this.init(q, DEFAULT_BLOCK_SIZE);
		}

		AudioTask(LinkedBlockingQueue<short[]> q, int block_size) {
			this.init(q, block_size);
		}

		void init(LinkedBlockingQueue<short[]> q, int block_size) {
			this.done = false;
			this.q = q;
			this.block_size = block_size;
			this.rec = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, 8000,
					AudioFormat.CHANNEL_IN_MONO,
					AudioFormat.ENCODING_PCM_16BIT, 8192);
		}

		public int getBlockSize() {
			return block_size;
		}

		public void setBlockSize(int block_size) {
			this.block_size = block_size;
		}

		public LinkedBlockingQueue<short[]> getQueue() {
			return q;
		}

		public void stop() {
			this.done = true;
		}

		public void run() {
			this.rec.startRecording();
			while (!this.done) {
				int nshorts = this.readBlock();
				if (nshorts <= 0)
					break;
			}
			this.rec.stop();
			this.rec.release();
		}

		int readBlock() {
			short[] buf = new short[this.block_size];
			int nshorts = this.rec.read(buf, 0, buf.length);
			if (nshorts > 0) {
				Log.d(getClass().getName(), "Posting " + nshorts
						+ " samples to queue");
				Message msg = mainHandler.obtainMessage(8, picNum++);
				if (picNum > 14) {
					picNum = 1;
				}
				mainHandler.sendMessage(msg);
				this.q.add(buf);
			}
			return nshorts;
		}
	}

	/**
	 * 状态定义，两种状态：闲置，监听
	 * @author LinLiangsheng
	 */
	enum State {
		IDLE, LISTENING
	};

	/**
	 *事件定义，四种事件：无，启动，停止， 关机
	 * @author LinLiangsheng
	 */
	enum Event {
		NONE, START, STOP, SHUTDOWN
	};

	/**
	 * 取得语音识别监听器对象
	 * @return 语音识别监听器对象
	 */
	public RecognitionListener getRecognitionListener() {
		return rl;
	}

	/**
	 * 设置语音识别监听器对象
	 * @param rl
	 */
	public void setRecognitionListener(RecognitionListener rl) {
		this.rl = rl;
	}

	/**
	 * 设置使用部分状态
	 * @param use_partials
	 */
	public void setUsePartials(boolean use_partials) {
		this.use_partials = use_partials;
	}

	/**
	 * 取得使用部分状态
	 * @return 使用部分状态
	 */
	public boolean getUsePartials() {
		return this.use_partials;
	}

	/**
	 * 构造方法，传入handler对象，用于更新语音图标UI
	 * @param handler
	 */
	public RecognizerTask(Handler handler) {
		mainHandler = handler;
		Config c = new Config();			//属性配置对象
		String sdPath  = Environment.getExternalStorageDirectory().getPath();		//获取SDCard路径，格式为:/mnt/sdcard
		System.out.println(sdPath);
		c.setString("-hmm", sdPath+"/Android/data/com.lins.smarthome/hmm/tdt_sc_8k");			//hmm全路径
		c.setString("-dict"   , sdPath+ "/Android/data/com.lins.smarthome/lm/smarthome.dic");	//dic全路径
		c.setString("-lm"     , sdPath+"/Android/data/com.lins.smarthome/lm/smarthome.lm");		//lm全路径
		c.setFloat("-samprate", 8000.0);
		c.setInt("-maxhmmpf", 2000);
		c.setInt("-maxwpf", 10);
		c.setInt("-pl_window", 2);
		c.setBoolean("-backtrace", true);
		c.setBoolean("-bestpath", false);
		this.ps = new Decoder(c);
		this.audio = null;
		this.audioq = new LinkedBlockingQueue<short[]>();
		this.use_partials = false;
		this.mailbox = Event.NONE;			//初始化事件为NONE
	}

	/**
	 * 线程执行方法
	 */
	public void run() {
		boolean done = false;			//控制是否跳出循环
		State state = State.IDLE;			//默认闲置状态
		String partial_hyp = null;			//暂存识别结果
		while (!done) {
			Event todo = Event.NONE;	//默认事件为无
			synchronized (this.mailbox) {
				todo = this.mailbox;
				if (state == State.IDLE && todo == Event.NONE) {
					try {
						Log.d(getClass().getName(), "waiting");
						this.mailbox.wait();
						todo = this.mailbox;
						Log.d(getClass().getName(), "got" + todo);
					} catch (InterruptedException e) {
						Log.e(getClass().getName(),"Interrupted waiting for mailbox, shutting down");
						todo = Event.SHUTDOWN;
					}
				}
				this.mailbox = Event.NONE;
			}
			/*根据事件进行不同处理*/
			switch (todo) {
			case NONE:			//无事件处理，无实质性处理
				if (state == State.IDLE)
					Log.e(getClass().getName(),"Received NONE in mailbox when IDLE, threading error?");
				break;
			case START:			//开启事件处理，状态闲置则设置监听，开语音识别
				if (state == State.IDLE) {
					Log.d(getClass().getName(), "START");
					this.audio = new AudioTask(this.audioq, 1024);
					this.audio_thread = new Thread(this.audio);
					this.ps.startUtt();
					this.audio_thread.start();
					state = State.LISTENING;
				} else
					Log.e(getClass().getName(),"Received START in mailbox when LISTENING");
				break;
			case STOP:			//停止事件处理，关语音识别，取得识别结果，退出循环
				if (state == State.IDLE)
					Log.e(getClass().getName(),"Received STOP in mailbox when IDLE");
				else {
					Log.d(getClass().getName(), "STOP");
					assert this.audio != null;
					this.audio.stop();
					try {
						this.audio_thread.join();
					} catch (InterruptedException e) {
						Log.e(getClass().getName(),"Interrupted waiting for audio thread, shutting down");
						done = true;
					}
					short[] buf;
					while ((buf = this.audioq.poll()) != null) {
						Log.d(getClass().getName(), "Reading " + buf.length + " samples from queue");
						this.ps.processRaw(buf, buf.length, false, false);
					}
					this.ps.endUtt();
					this.audio = null;
					this.audio_thread = null;

					Hypothesis hyp = this.ps.getHyp();
					if (this.rl != null) {
						if (hyp == null) {
							Log.d(getClass().getName(), "Recognition failure");
							this.rl.onError(-1);
						} else {
							Bundle b = new Bundle();
							Log.e(getClass().getName(),"zs log Final hypothesis: " + hyp.getHypstr());
							Log.e(getClass().getName(), ps.getUttid());
							b.putString("hyp", hyp.getHypstr());
							this.rl.onResults(b);
						}
					}
					state = State.IDLE;
				}
				break;
			case SHUTDOWN:	//关机事件处理
				Log.d(getClass().getName(), "SHUTDOWN");
				if (this.audio != null) {
					this.audio.stop();
					assert this.audio_thread != null;
					try {
						this.audio_thread.join();
					} catch (InterruptedException e) {
					}
				}
				this.ps.endUtt();
				this.audio = null;
				this.audio_thread = null;
				state = State.IDLE;
				done = true;
				break;
			}
			if (state == State.LISTENING) {
				assert this.audio != null;
				try {
					short[] buf = this.audioq.take();
					Log.d(getClass().getName(), "Reading " + buf.length + " samples from queue");
					this.ps.processRaw(buf, buf.length, false, false);
					Hypothesis hyp = this.ps.getHyp();
					if (hyp != null) {
						String hypstr = hyp.getHypstr();
						if (hypstr != partial_hyp) {
							Log.d(getClass().getName(),"Hypothesis: " + hyp.getHypstr() + " Uttid: " + hyp.getUttid() + " Best_score: " + hyp.getBest_score());
							if (this.rl != null && hyp != null) {
								Bundle b = new Bundle();
								b.putString("hyp", hyp.getHypstr());
//								this.rl.onPartialResults(b);
							}
						}
						partial_hyp = hypstr;
					}
				} catch (InterruptedException e) {
					Log.d(getClass().getName(), "Interrupted in audioq.take");
				}
			}
		}
	}

	/**
	 * 语音识别开始方法
	 */
	public void start() {
		Log.d(getClass().getName(), "signalling START");
		synchronized (this.mailbox) {
			this.mailbox.notifyAll();
			Log.d(getClass().getName(), "signalled START");
			this.mailbox = Event.START;
		}
	}

	/**
	 * 语音识别停止方法
	 */
	public void stop() {
		Log.d(getClass().getName(), "signalling STOP");
		synchronized (this.mailbox) {
			this.mailbox.notifyAll();
			Log.d(getClass().getName(), "signalled STOP");
			this.mailbox = Event.STOP;
		}
	}

	/**
	 * 语音识别关机方法
	 */
	public void shutdown() {
		Log.d(getClass().getName(), "signalling SHUTDOWN");
		synchronized (this.mailbox) {
			this.mailbox.notifyAll();
			Log.d(getClass().getName(), "signalled SHUTDOWN");
			this.mailbox = Event.SHUTDOWN;
		}
	}
}