import os
import json
import re
from dotenv import load_dotenv
import google.generativeai as genai
from google.api_core.exceptions import GoogleAPIError

# （可选）代理配置
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

# 注意：保持 assetss（用户特意设定）
EN_FILE = os.path.join("src", "generated", "resources", "assetss", "pathosyn", "lang", "en_us.json")
ZH_FILE = os.path.join("src", "generated", "resources", "assetss", "pathosyn", "lang", "zh_cn.json")
REGISTRY_DIR = os.path.join("src", "main", "java")

# 加载语言数据
os.makedirs(os.path.dirname(EN_FILE), exist_ok=True)
en_data = {}
zh_data = {}
if os.path.exists(EN_FILE):
    with open(EN_FILE, encoding="utf-8") as f:
        en_data = json.load(f)
if os.path.exists(ZH_FILE):
    with open(ZH_FILE, encoding="utf-8") as f:
        zh_data = json.load(f)

# 匹配 translatable(...) 和 .register("xxx")
TRANSLATABLE_PATTERN = re.compile(r'translatable\s*\(\s*"([^"]+)"')
REGISTER_PATTERN = re.compile(r'\.register\s*\(\s*"([^"]+)"')

# 遍历源码
for root, _, files in os.walk(REGISTRY_DIR):
    for file in files:
        if file.endswith(".java"):
            file_path = os.path.join(root, file)
            print(f" 正在处理: {file_path}")
            with open(file_path, encoding="utf-8") as f:
                content = f.read()

                for key in TRANSLATABLE_PATTERN.findall(content):
                    if key not in en_data:
                        print(f" 添加 translatable 键: {key}")
                        en_data[key] = "TODO"
                        if key not in zh_data:
                            zh_data[key] = "TODO"

                for item_name in REGISTER_PATTERN.findall(content):
                    lang_key = f"item.pathosyn.{item_name}"
                    if lang_key not in en_data:
                        print(f" 添加注册物品键: {lang_key}")
                        en_data[lang_key] = "TODO"
                        if lang_key not in zh_data:
                            zh_data[lang_key] = "TODO"

# 翻译部分，仅对 en=TODO 的进行补全
for key, en_text in en_data.items():
    zh_text = zh_data.get(key, "TODO")

    # 若已有英文翻译则跳过
    if en_text != "TODO":
        continue

    # 若已有中文翻译但英文缺失，则尝试自动生成英文（美化）
    if zh_text != "TODO":
        en_data[key] = key.split(".")[-1].replace("_", " ").title()
        print(f" 填充英文: {key} -> {en_data[key]}")
        continue

    try:
        if key.startswith(("item.", "block.", "entity.", "effect.", "key.", "tooltip.")):
            prompt = f"这是 Minecraft 模组中注册物品/方块的名称，请翻译为简体中文，仅返回翻译：\n\n{key.split('.')[-1]}"
        else:
            prompt = f"请将以下 Minecraft 文本翻译为简体中文，仅返回翻译：\n\n{key}"

        response = model.generate_content(prompt)
        translated = response.text.strip().replace('"', '')

        # ✅ 不覆盖已有中文
        if zh_data.get(key) == "TODO":
            zh_data[key] = translated
            print(f" 翻译: {key} -> {translated}")
        else:
            print(f" 已存在翻译（跳过）: {key} -> {zh_data[key]}")

    except GoogleAPIError as e:
        print(f" API 错误: {e}")
        zh_data[key] = "TODO"
    except Exception as e:
        print(f" 翻译失败: {e}")
        zh_data[key] = "TODO"

# 写入文件
with open(EN_FILE, "w", encoding="utf-8") as f:
    json.dump(en_data, f, ensure_ascii=False, indent=2)
with open(ZH_FILE, "w", encoding="utf-8") as f:
    json.dump(zh_data, f, ensure_ascii=False, indent=2)

print("\n ✅ 所有翻译处理完成，语言文件已写入。")
