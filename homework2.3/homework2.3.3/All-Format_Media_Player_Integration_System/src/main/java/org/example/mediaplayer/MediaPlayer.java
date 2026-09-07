package org.example.mediaplayer;

/**
 * 标准媒体播放器接口
 * 定义公司现有的统一播放接口
 */
public interface MediaPlayer {

    /**
     * 播放指定类型的音频文件
     *
     * @param audioType 音频类型（如 mp3, flac, wav）
     * @param fileName  文件名
     */
    void play(String audioType, String fileName);
}
