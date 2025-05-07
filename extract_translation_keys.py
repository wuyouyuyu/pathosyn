import os
import json
import openai

openai.api_key = os.getenv("OPENAI_API_KEY")
EN_FILE = os.path.join("src", "generated", "resources", "assets", "pathosyn", "lang", "en_us.json")
ZH_FILE = os.path.join("src", "generated", "resources", "assets", "pathosyn", "lang", "zh_cn.json")

with open(EN_FILE, encoding="utf-8") as f:
    en_data = json.load(f)

# 加载已有 zh 文件（支持增量更新）
if os.path.exists(ZH_FILE):
    with open(ZH_FILE, encoding="utf-8") as f:
        zh_data = json.load(f)
else:
    zh_data = {}

for key, text in en_data.items():
    if key in zh_data and zh_data[key] != "TODO":
        continue

    print(f"正在翻译: {text}")

    try:
        response = openai.ChatCompletion.create(
            model="gpt-3.5-turbo",
            messages=[
                {"role": "system", "content": "你是一个Minecraft模组语言翻译助手，要求输出简体中文。"},
                {"role": "user", "content": f"请翻译以下文本：{text}"}
            ]
        )
        zh_data[key] = response["choices"][0]["message"]["content"].strip()
    except Exception as e:
        print(f"翻译失败: {e}")
        zh_data[key] = "TODO"

with open(ZH_FILE, "w", encoding="utf-8") as f:
    json.dump(zh_data, f, ensure_ascii=False, indent=2)

print(f"已翻译并写入 {ZH_FILE}")