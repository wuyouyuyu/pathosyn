import os
import re
import json
from dotenv import load_dotenv
import google.generativeai as genai
from google.api_core.exceptions import GoogleAPIError

# 配置代理（可选）
os.environ["HTTP_PROXY"] = "http://127.0.0.1:7890"
os.environ["HTTPS_PROXY"] = "http://127.0.0.1:7890"

# 加载 API Key
load_dotenv()
api_key = os.getenv("GEMINI_API_KEY")
if not api_key:
    print("未检测到 GEMINI_API_KEY，请在 .env 中设置。")
    exit(1)

# 初始化 Gemini
genai.configure(api_key=api_key)
model = genai.GenerativeModel("models/gemini-1.5-flash-latest")

# 测试 Gemini 连通性
def test_gemini_api():
    print(" 正在测试 Gemini API 连通性...")
    try:
        response = model.generate_content("请简短回复，以测试 API 是否正常。")
        print(f" Gemini API 测试成功：{response.text.strip()}")
        return True
    except Exception as e:
        print(f" Gemini API 测试失败：{e}")
        return False

if not test_gemini_api():
    exit(1)

# 文件路径
# 文件路径（注意 assetss）
EN_FILE = os.path.join("src", "generated", "resources", "assetss", "pathosyn", "lang", "en_us.json")
ZH_FILE = os.path.join("src", "generated", "resources", "assetss", "pathosyn", "lang", "zh_cn.json")


REGISTRY_DIR = os.path.join("src", "main", "java")

# 加载语言文件
os.makedirs(os.path.dirname(EN_FILE), exist_ok=True)
en_data = {}
zh_data = {}

if os.path.exists(EN_FILE):
    with open(EN_FILE, encoding="utf-8") as f:
        en_data = json.load(f)
if os.path.exists(ZH_FILE):
    with open(ZH_FILE, encoding="utf-8") as f:
        zh_data = json.load(f)

# 正则提取器
TRANSLATABLE_PATTERN = re.compile(r'translatable\s*\(\s*"([^"]+)"')
REGISTER_PATTERN = re.compile(r'registerItem\s*\(\s*"([^"]+)"')

# 遍历源码提取键
for root, _, files in os.walk(REGISTRY_DIR):
    for file in files:
        if file.endswith(".java"):
            with open(os.path.join(root, file), encoding="utf-8") as f:
                content = f.read()

                for key in TRANSLATABLE_PATTERN.findall(content):
                    if key not in en_data:
                        print(f" 添加 translatable 键: {key}")
                        en_data[key] = "TODO"
                        zh_data[key] = "TODO"

                for item_name in REGISTER_PATTERN.findall(content):
                    key = f"item.pathosyn.{item_name}"
                    if key not in en_data:
                        print(f" 添加注册物品键: {key}")
                        en_data[key] = "TODO"
                        zh_data[key] = "TODO"

# 翻译所有 TODO 项
for key, en_text in en_data.items():
    zh_text = zh_data.get(key, "TODO")

    # 如果中文已翻译但英文为 TODO，则自动反填
    if en_text == "TODO" and zh_text != "TODO":
        en_data[key] = key.split(".")[-1].replace("_", " ").title()
        print(f" 填充英文: {key} -> {en_data[key]}")
        continue

    # 若已有英文翻译则跳过
    if en_text != "TODO":
        continue

    try:
        if key.startswith(("item.", "block.", "entity.", "effect.", "key.", "tooltip.")):
            prompt = f"这是 Minecraft 模组中注册物品/方块的名称，请翻译为简体中文，仅返回翻译：\n\n{key.split('.')[-1]}"
        else:
            prompt = f"请将以下 Minecraft 文本翻译为简体中文，仅返回翻译：\n\n{key}"

        response = model.generate_content(prompt)
        translated = response.text.strip().replace('"', '')
        zh_data[key] = translated
        print(f" 翻译: {key} -> {translated}")
    except GoogleAPIError as e:
        print(f" API 错误: {e}")
        zh_data[key] = "TODO"
    except Exception as e:
        print(f" 翻译失败: {e}")
        zh_data[key] = "TODO"

# 保存结果
with open(EN_FILE, "w", encoding="utf-8") as f:
    json.dump(en_data, f, ensure_ascii=False, indent=2)

with open(ZH_FILE, "w", encoding="utf-8") as f:
    json.dump(zh_data, f, ensure_ascii=False, indent=2)

print("\n ✅ 所有翻译处理完成，语言文件已写入。")
