package org.example.mediaplayer;

/**
 * 媒体播放器工厂 —— 工厂方法模式
 * 根据音频格式后缀名，返回对应的 MediaPlayer 实例
 * 客户端不直接 new 任何具体播放器类，完全通过工厂获取对象
 */
public class MediaPlayerFactory {

    /**
     * 根据音频类型获取对应的播放器实例
     *
     * @param audioType 音频格式后缀（如 "mp3", "flac", "wav"）
     * @return 对应的 MediaPlayer 实例
     */
    public static MediaPlayer getPlayer(String audioType) {
        return switch (audioType.toLowerCase()) {
            case "mp3" -> new Mp3Player();
            case "flac", "wav" -> new MediaAdapter(audioType);
            default -> {
                System.out.println("[Factory] 不支持的音频格式: " + audioType);
                yield new MediaAdapter("unknown"); // 返回适配器以保持接口一致性
            }
        };
    }
}
