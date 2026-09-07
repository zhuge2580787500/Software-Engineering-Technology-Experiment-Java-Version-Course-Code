package org.example.mediaplayer;

/**
 * MP3 播放器 —— MediaPlayer 接口的直接实现
 */
public class Mp3Player implements MediaPlayer {

    @Override
    public void play(String audioType, String fileName) {
        if ("mp3".equalsIgnoreCase(audioType)) {
            System.out.println("[Mp3Player] 正在播放 MP3 文件: " + fileName);
        } else {
            System.out.println("[Mp3Player] 格式 " + audioType + " 不受支持，请使用 MediaAdapter 进行适配播放。");
        }
    }
}
