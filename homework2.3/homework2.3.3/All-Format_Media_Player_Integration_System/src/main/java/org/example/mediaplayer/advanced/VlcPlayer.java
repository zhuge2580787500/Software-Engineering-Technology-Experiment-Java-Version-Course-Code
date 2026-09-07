package org.example.mediaplayer.advanced;

/**
 * VLC 播放器 —— 第三方高级音频播放器的具体实现
 * 模拟第三方闭源库，实现 AdvancedAudioPlayer 接口
 */
public class VlcPlayer implements AdvancedAudioPlayer {

    @Override
    public void playFlac(String fileName) {
        System.out.println("[VlcPlayer] 正在播放 FLAC 文件（原生支持）: " + fileName);
    }

    @Override
    public void playWav(String fileName) {
        System.out.println("[VlcPlayer] 正在播放 WAV 文件（原生支持）: " + fileName);
    }
}
