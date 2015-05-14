package com.lins.smarthome;
/**
 * 语音识别监听器接口，提供抽象方法
 * @author LinLiangsheng
 */
import android.os.Bundle;

public interface RecognitionListener {
	abstract void onResults(Bundle b);		//识别结果
	abstract void onError(int err);				//出错处理
}