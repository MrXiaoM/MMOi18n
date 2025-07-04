package top.mrxiaom.mmoi18n.placeholder;

import org.jetbrains.annotations.Nullable;

/**
 * 变量提供器
 */
public interface IProvider<ItemStat> {
    /**
     * 返回 DELETE_FLAG 代表该变量所在的一整行都删除
     */
    String DELETE_FLAG = "\33:delete";

    /**
     * 替换一行 Lore
     * @param stat 物品Stat
     * @param raw 物品随机数据 (RandomStatData)
     * @param line 要替换的行
     * @return 替换后的 Lore，可返回多行，使用<code>\n</code>分隔，<br>
     * 返回 <code>null</code> 代表未替换，<br>
     * 返回 {@link IProvider#DELETE_FLAG} 代表删除这一行
     */
    @Nullable
    String replaceLine(ItemStat stat, Object raw, String line);

}
