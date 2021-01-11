(ns yuyin-baidu.rest-way
  (:require
   [clojure.string :as string]
   [clj-http.client :as http]
   [clojure.data.json :as json]
   [ring.util.codec :as codec]
   [yuyin-baidu.token :as token]
   [clojure.string :as str]
   [clojure.java.io :as io]
   )
  (:import
   [org.apache.commons.io IOUtils]))


;; Automatic Speech Recognition

;; 需要识别的文件
(def ^:private file-name "16k.wav")

;; string	必填	用户唯一标识，用来区分用户，计算UV值。建议填写能区分用户的机器 MAC 地址或 IMEI 码，长度为60字符以内。
(def ^:private cuid "longlongtimeagothereisamiao")

;;采样率固定值
(def ^:private rate 16000)

(def ^:private file-format (last (string/split file-name #"\.")))

(defn- content-type-str [file-format rate]
  (format "audio/%s; rate=%s" file-format (str rate)))

;; 普通版 参数
(def asr-normal-config
  {:url "http://vop.baidu.com/server_api" ; // 可以改为https
   ;; //  1537 表示识别普通话，使用输入法模型。 其它语种参见文档
   :dev_pid 1537                        ;
   :scope "audio_voice_assistant_get"   ;
   })

;; 自训练平台 参数
;; (def asr-self-trainning-config
;;   {;; //自训练平台模型上线后，您会看见 第二步：“”获取专属模型参数pid:8001，modelid:1234”，按照这个信息获取 dev_pid=8001，lm_id=1234
;;    :dev_pid  8001                       ;
;;    :lm_id  1234；
;;    })

;; /* 极速版 参数
;; (def enhanced-asr-config
;;   {:url   "http://vop.baidu.com/pro_api"      ; // 可以改为https
;;    :dev_pid 80001                             ;
;;    :scope "brain_enhanced_asr"                ;
;;    })

;; TODO not right
(defn file->bytes [file]
  (with-open [xin (io/input-stream file)
              xout (java.io.ByteArrayOutputStream.)]
    (io/copy xin xout)
    (.toByteArray xout))
  )

;; (defn file-to-byte-array
;;   [^java.io.File file]
;;   (let [result (byte-array (.length file))]
;;     (with-open [in (java.io.DataInputStream. (clojure.java.io/input-stream file))]
;;       (.readFully in result))
;;     result))

;; (count (file-to-byte-array (io/file (io/resource "16k.wav"))))


;; TODO not right
(defn file-content [file-name]
  (file->bytes (io/file (io/resource file-name))))

(defn base64-content [file-name]
  (codec/base64-encode (file->bytes (io/file (io/resource file-name)))))

;; RAW方式
;; 音频文件，读取二进制内容后，直接放在 body 中。
;; Content-Length 的值即为音频文件的大小。（一般代码会自动生成）。
;; 由于使用 raw 方式， 采样率和文件格式需要填写在 Content-Type 中
;;    Content-Type: audio/pcm;rate=16000
(defn raw-post [token]
  (let [{:keys [url dev_pid lm_id]} asr-normal-config
        composed-url                (format "%s?dev_pid=%s&cuid=%s&token=%s" url dev_pid (codec/url-encode cuid) token)
        content-type-str            (content-type-str file-format rate)
        content                     (file-content file-name)]
    (http/post composed-url {:content-type content-type-str
                             :body content})))

;; (raw-post (token/get-access-token))


;; JSON 方式
;; 音频文件，读取二进制内容后，进行 base64 编码后放在 speech 参数内。
;; 音频文件的原始大小, 即二进制内容的字节数，填写 “len” 字段
;; 由于使用 json 格式， header 为：
;; Content-Type:application/json
(defn json-post [token]
  (let [{:keys [url dev_pid lm_id]} asr-normal-config
        file   (io/file (io/resource file-name))
        len    (.length file)
        speech (base64-content file-name) ;; TODO not right
        data   {:format  "wav" ;string 必填 语音文件的格式，pcm/wav/amr/m4a。不区分大小写。推荐pcm文件
                :rate    16000 ;int 必填 采样率，16000、8000，固定值
                :channel 1 ;int	必填 声道数，仅支持单声道，请填写固定值 1
                :cuid    cuid ;string 必填 用户唯一标识，用来区分用户，计算UV值。建议填写能区分用户的机器 MAC 地址或 IMEI 码，长度为60字符以内。
                :token   token ;string 必填 开放平台获取到的开发者[access_token]获取 Access Token "access_token")
                :dev_pid 1537 ;int 选填 不填写lan参数生效，都不填写，默认1537（普通话 输入法模型），dev_pid参数见本节开头的表格
                ;; :lm_id ;int 选填 自训练平台模型id，填dev_pid = 8001 或 8002生效
                ;; :lan   ;string 选填，废弃参数 历史兼容参数，已不再使用
                :speech  speech ;string	必填 本地语音文件的二进制语音数据 ，需要进行base64 编码。与len参数连一起使用。
                :len     len ;int 必填 本地语音文件的的字节数，单位字节
                }]
    (http/post url {:body         (json/write-str data)
                    :content-type :json})))

;; (json-post (token/get-access-token))
