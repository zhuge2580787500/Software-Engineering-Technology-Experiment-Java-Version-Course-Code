package org.example.mediaplayer.advanced;

/**
 * 第三方高级音频播放器接口（模拟闭源库）
 * 提供播放 FLAC 和 WAV 格式的能力
 */
public interface AdvancedAudioPlayer {

    /**
     * 播放 FLAC 格式文件
     *
     * @param fileName 文件名
     */
    void playFlac(String fileName);

    /**
     * 播放 WAV 格式文件
     *
     * @param fileName 文件名
     */
    void playWav(String fileName);
}
