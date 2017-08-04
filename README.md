# SimuViewer

這是個簡單的App，主要用來驗證interface leltek.viewer.model.Probe的設計是否正確可行。

## 設計
* 在設計上，interface中的function如果實作底層需要跟device通訊，call完之後必須使用callback來得到最終的結果，這樣才不會影響到UI畫面的流暢而不會發生hang住的情形
* leltek.viewer.model.Probe中所有的callback都已經切換到UI thread，所以可以直接更新UI component的內容
* 影像的來源並非實際的device，而是從單一檔案讀入並parse後所得到的，並且會不停的loop
* cine buffer是用來存最新的Frame, 最多存100個。當cine buffer滿了，為了存最新的Frame，會將buffer中最舊的Frame移除
* Frame不僅存有一張影像，還包含這張影像所需的data
* 為了簡化實作，只做landscape

## 畫面
* 畫面只有兩個，一個是MainActivity, 另一個是ScanActivity
* MainActivity啟動後會自動模擬device connect的動作，只有當connected後，按scan button才能成功進入ScanActivity畫面
* ScanActivity啟動後會自動開始scan，會看到cine buffer的個數不斷增加，直到100後cine buffer的個數就不會再增加了，而是重用buffer的空間，此時影像還是有在更新
* 因為是錄假體的影像，所以看起來好像畫面沒有更新，細看之下，影像是有在更新的。
* ScanActivity的影像可以簡單的drag & move及zoom in & out
* 可以按Test開頭的button來模擬device遇到的狀況。
* Test Conn Error是用來模擬device斷線的狀況，畫面會回到MainActivity, 可以按Connect來重新連線
