package org.example.mediaplayer;

import org.example.mediaplayer.advanced.AdvancedAudioPlayer;
import org.example.mediaplayer.advanced.VlcPlayer;

/**
 * 媒体播放器适配器 —— 适配器模式的核心
 * 将第三方高级音频播放器（AdvancedAudioPlayer）适配为标准的 MediaPlayer 接口
 * 使得 FLAC 和 WAV 格式的播放也能通过统一的 play() 方法完成
 */
public class MediaAdapter implements MediaPlayer {

    /** 内部持有的第三方高级音频播放器实例 */
    private final AdvancedAudioPlayer advancedAudioPlayer;

    /**
     * 构造方法：根据音频类型选择对应的适配策略
     *
     * @param audioType 音频格式（flac / wav）
     */
    public MediaAdapter(String audioType) {
        // 客户端不需要知道 VlcPlayer 的存在，由适配器负责内部实例化
        this.advancedAudioPlayer = new VlcPlayer();
    }

    /**
     * 将标准 play 方法的请求适配委托给 AdvancedAudioPlayer
     *
     * @param audioType 音频类型
     * @param fileName  文件名
     */
    @Override
    public void play(String audioType, String fileName) {
        if ("flac".equalsIgnoreCase(audioType)) {
            advancedAudioPlayer.playFlac(fileName);
        } else if ("wav".equalsIgnoreCase(audioType)) {
            advancedAudioPlayer.playWav(fileName);
        } else {
            System.out.println("[MediaAdapter] 不支持的音频格式: " + audioType);
        }
    }
}
