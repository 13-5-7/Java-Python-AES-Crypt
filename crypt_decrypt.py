from Crypto.Cipher import AES
import base64
import sys
import os

# AESのブロックサイズ（16バイト）に合わせてパディングを追加
def pad(byte_array):
    BLOCK_SIZE = 16
    pad_len = BLOCK_SIZE - len(byte_array) % BLOCK_SIZE
    return byte_array + (bytes([pad_len]) * pad_len)

# 復号後に末尾のパディングを削除
def unpad(byte_array):
    last_byte = byte_array[-1]
    return byte_array[0:-last_byte]

def encrypt(key, message):
    """
    文字列を暗号化し、Base64エンコードされた文字列を返す
    """

    # メッセージをUTF-8バイト列に変換
    byte_array = message.encode("UTF-8")

    # パディング処理
    padded = pad(byte_array)

    # ランダムなIV（初期化ベクトル）を生成（16バイト）
    iv = os.urandom(AES.block_size)

    # AES CBCモードで暗号化器を作成
    cipher = AES.new( key.encode("UTF-8"), AES.MODE_CBC, iv )
    encrypted = cipher.encrypt(padded)
    
    # IV(16byte) + 暗号文 を結合してBase64で返す
    return base64.b64encode(iv+encrypted).decode("UTF-8")

def decrypt(key, message):
    """
    Base64文字列を復号し、元の文字列を返す
    """
    
    # Base64をデコードしてバイト列に戻す
    byte_array = base64.b64decode(message)

    # 先頭16バイトからIVを抽出
    iv = byte_array[0:16]

    # 16バイト目以降が暗号化された本体
    messagebytes = byte_array[16:]
    
    # 暗号化時と同じ鍵とIV、モードを設定
    cipher = AES.new(key.encode("UTF-8"), AES.MODE_CBC, iv )

    # 復号実行
    decrypted_padded = cipher.decrypt(messagebytes)

    # パディングを取り除く
    decrypted = unpad(decrypted_padded)

    return decrypted.decode('shift_jis')

def main():
    key = "sampleKeyForAES_"
    message = "eqmZaHrW/LX2/6lL2aG+bn+AgPYgBeYzJ69wOMtC0VbAUHafdEsjNcyH58uMLjWi"
    # message = "テストメッセージ"

    # print(encrypt(key,message))
    print(decrypt(key,message))

main()