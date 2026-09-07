package org.example.mediaplayer;

/**
 * 客户端测试类 —— Main 方法
 * 模拟客户端调用，验证工厂方法模式 + 适配器模式的集成效果
 * 客户端不直接 new 任何具体播放器，完全通过 MediaPlayerFactory 获取对象
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("    全格式媒体播放器集成系统 - 测试   ");
        System.out.println("========================================");
        System.out.println();

        // 测试 1：播放 MP3 格式文件 —— 使用原生 Mp3Player
        System.out.println(">>> 测试 1：播放 song.mp3");
        MediaPlayer mp3Player = MediaPlayerFactory.getPlayer("mp3");
        mp3Player.play("mp3", "song.mp3");
        System.out.println();

        // 测试 2：播放 FLAC 格式文件 —— 通过 MediaAdapter 适配 VlcPlayer
        System.out.println(">>> 测试 2：播放 lossless.flac");
        MediaPlayer flacPlayer = MediaPlayerFactory.getPlayer("flac");
        flacPlayer.play("flac", "lossless.flac");
        System.out.println();

        // 测试 3：播放 WAV 格式文件 —— 通过 MediaAdapter 适配 VlcPlayer
        System.out.println(">>> 测试 3：播放 record.wav");
        MediaPlayer wavPlayer = MediaPlayerFactory.getPlayer("wav");
        wavPlayer.play("wav", "record.wav");
        System.out.println();

        // 测试 4：边界测试 —— 不支持的格式
        System.out.println(">>> 测试 4：播放不支持的格式（边界测试）");
        MediaPlayer unknownPlayer = MediaPlayerFactory.getPlayer("ogg");
        unknownPlayer.play("ogg", "audio.ogg");
        System.out.println();

        System.out.println("========================================");
        System.out.println("    所有测试完成！                        ");
        System.out.println("========================================");
    }
}
