/**
 * ============================================================
 * JSON 序列化工具 ({@code JsonUtil})
 * ============================================================
 * <p>
 * 基于 Jackson 库的工具类，用于将清洗后的遥测记录列表
 * 序列化为标准 JSON 格式并写入文件。
 * </p>
 * <p>
 * 输出文件格式要求 (exam1.md 规范)：
 * </p>
 * <ul>
 *   <li>标准的 JSON 数组格式</li>
 *   <li>每个对象包含: {@code timestamp}, {@code roverId}, {@code roverType},
 *       {@code sensorType}, {@code value}, {@code status}</li>
 *   <li>文件编码: UTF-8</li>
 *   <li>美化输出: 2 空格缩进</li>
 * </ul>
 * <p>
 * 为什么使用 Jackson 而非手动拼接字符串：
 * </p>
 * <ul>
 *   <li>保证 JSON 格式的正确性和完整性</li>
 *   <li>自动处理特殊字符转义 (引号、换行等)</li>
 *   <li>符合 exam1.md 中"严禁手动拼接字符串"的要求</li>
 * </ul>
 * ============================================================
 */
package com.deepspace.util;

import com.deepspace.factory.TelemetryRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JsonUtil {

    /** Jackson 对象映射器 (线程安全，可复用) */
    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        // 启用美化输出: 2 空格缩进，方便阅读
        OBJECT_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
        // 确保日期等类型按标准格式序列化
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * 将遥测记录列表序列化为 JSON 并写入指定文件。
     *
     * @param records 清洗后的有效记录列表
     * @param filePath 输出文件路径 (相对于项目根目录或绝对路径)
     * @throws IOException 如果文件写入失败
     */
    public static void writeToFile(List<TelemetryRecord> records, String filePath) throws IOException {
        File outputFile = new File(filePath);

        // 如果输出目录不存在，自动创建
        File parentDir = outputFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        // 将记录列表写入 JSON 文件
        OBJECT_MAPPER.writeValue(outputFile, records);
    }
}
