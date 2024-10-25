# MMOItems 翻译计划

本插件最低需要 `Java 17`，适用于 MMOItems `6.10`，其余版本请自行尝试。

这个项目我是边骂作者边写的，作为一个售价高达 **€18.29** 的付费插件，~~都快赶上降价之前的 Minecraft Java Edition 了~~，居然连本地化都不做好，编辑器的文字全都是硬编码的。如果插件更新了，还要重新翻译一遍，麻烦的一。

本插件会禁止 MMOItems 打开它自己的物品浏览器、物品编辑器等界面，转而打开本插件修改好的界面。你可以在配置文件 `config.yml` 设定界面中的大部分文本。

可惜的是，在聊天栏的提示信息无法替换，这些消息在 ItemStat 实现的界面点击逻辑 (`whenClciked`, `whenInput` 等) 直接通过 `sendMessage` 被发给玩家。除了使用修改发包替换文本这种低效的方案，我们实在没有办法在不动原插件的情况下优雅地替换掉聊天提示信息。

> 这是个正在进行中的项目。  
> 计划先将「判断物品名执行操作」改为「判断物品NBT执行操作」，同时汉化硬编码的字符串，在插件能够稳定运行后再将其转移到配置文件。

## 命令

主命令为 `/mmoi18n`，别名 `/mii`。

```
/mii reload -- 重载配置文件
```
