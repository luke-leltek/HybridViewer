# SimuViewer

這是個簡單的 App，主要用來驗證 interface leltek.viewer.model.Probe 的設計是否正確可行。

## 設計
* 在設計上，interface 中的 function 如果實作底層需要跟 device 通訊，call 完之後必須使用 callback 來得到最終的結果，這樣才不會影響到 UI 畫面的流暢而不會發生 hang 住的情形。
* leltek.viewer.model.Probe 中所有的 callback 都已經切換到 UI thread，所以可以直接更新 UI component 的內容。
* 影像的來源為亂數。
* cine buffer 是用來存最新的 Frame，最多存 50 個。當 cine buffer 滿了，為了存最新的 Frame，會將 buffer 中最舊的 Frame 移除。
* Frame 不僅存有一張影像，還包含這張影像所需的 data。
* 為了簡化實作，只做 landscape。

## 畫面
* 畫面只有兩個，一個是 MainActivity，另一個是 ScanActivity。
* MainActivity 啟動後會自動模擬 device connect 的動作，只有當 connected 後，按 scan button 才能成功進入 ScanActivity 畫面。
* ScanActivity 啟動後會自動開始 scan，會看到 cine buffer 的個數不斷增加，直到 50 後 cine buffer 的個數就不會再增加了，而是重用 buffer 的空間，此時影像還是有在更新。
* ScanActivity 的影像可以簡單的 drag & move 及 zoom in & out。
* 可以按 Test 開頭的 button 來模擬 device 遇到的狀況。
* Test Conn Error 是用來模擬 device 斷線的狀況，畫面會回到 MainActivity，可以按 Connect 來重新連線。
"# HybridViewer" 
