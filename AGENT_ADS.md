# Android Development Agents - Ads Integration Rules & Skill Set (`AGENT_ADS.md`)

Tài liệu này định nghĩa bộ quy tắc, kỹ năng (Skill Set) và quy trình tự động hóa cho AI Agent khi thực hiện gắn quảng cáo trên dự án **BasePhamThang** dựa theo kịch bản trên **Google Sheet**.

---

## 1. Quy trình Tự động hóa khi Nhận Link Google Sheet

Khi người dùng yêu cầu gắn quảng cáo và cung cấp link Google Sheet (ví dụ: `https://docs.google.com/spreadsheets/d/<SHEET_ID>/edit...`):

1. **Đọc & Trích xuất Kịch bản từ Google Sheet**:
   - Tải dữ liệu từ Google Sheet qua URL export: `https://docs.google.com/spreadsheets/d/<SHEET_ID>/export?format=xlsx` (hoặc `format=csv`).
   - Phân tích đầy đủ các cột trong bảng:
     - `No.`: Số thứ tự vị trí quảng cáo.
     - `Màn`: Tên màn hình hoặc cụm chức năng.
     - `New Ad Unit Name`: Tên định danh đơn vị quảng cáo -> Map vào `Constants.AdsKey`.
     - `Ad Type`: Loại quảng cáo (`Open`, `Appopen`, `Inter` / `INTERSTITIAL`, `Native` / `native`, `Reward`, `Banner`, `Collapse`).
     - `Placement`: Mô tả vị trí đặt, kích thước, vị trí nút CTA, kịch bản hiển thị và các **Remote Config Key phụ** (interval, chẵn/lẻ...).
     - `Flow`: Luồng hoạt động chi tiết hoặc cấu hình thời gian reload riêng (ví dụ: `reload_interval_native_dropdown`).
     - `Remote Config`: Trạng thái mặc định (`Bật` / `Tắt`).
     - `Data type`: Kiểu dữ liệu Remote Config (`Boolean`, `Long`, `String`).
     - `Remote Key`: Tên khóa điều khiển từ xa -> Map vào `Constants.RemoteKeys`.

2. **Đồng bộ `Constants.AdsKey` & `Constants.RemoteKeys` (`Constants.kt`)**:
   - Đối chiếu toàn bộ `New Ad Unit Name` và `Remote Key` trong Sheet với file `app/src/main/java/com/phamthang/base/utils/Constants.kt`.
   - Tự động bổ sung tất cả các key còn thiếu vào `object AdsKey` và `object RemoteKeys`.
   - Quét nội dung cột `Placement` và `Flow` để trích xuất các key cấu hình phụ (như `inter_ad_episode_interval`, `natibe_ad_episode_interval`, `even_or_odd_inter_next_episode`, `reload_interval_native_dropdown`, `show_ads`...) và thêm vào `Constants.RemoteKeys`.
   - Cập nhật danh sách `addListTurnOffRemoteKeys()` trong `SplashActivity.kt` cho các `RemoteKeys` quảng cáo mới được thêm.

3. **Triển khai Logic Quảng cáo qua `AdsHelper`**:
   - Tuyệt đối không gắn trùng lặp các quảng cáo đã có sẵn trong `ui.default_ui`.
   - Tại các màn hình chức năng (`ui.feature`), mọi lời gọi quảng cáo **BẮT BUỘC** thông qua `AdsHelper` (và mở rộng `AdsHelper` nếu có biến thể mới từ kịch bản).

---

## 2. Các Quảng cáo Đã Gắn Sẵn tại `ui.default_ui` (Không gắn lại trừ khi có yêu cầu sửa)

Hệ thống Base đã tích hợp sẵn toàn bộ luồng quảng cáo khởi đầu trong package `com.phamthang.base.ui.default_ui`:

| Màn hình | Class | AdsKey / RemoteKey đã tích hợp | Logic đã xử lý |
| :--- | :--- | :--- | :--- |
| **Splash** | `SplashActivity` | `open_splash`, `inter_splash`, `banner_setting`, `native_language`, `native_click` | Khởi tạo `AsyncSplash`, quản lý danh sách `addListTurnOffRemoteKeys()`, cấu hình `WelcomeBackActivity`, load `banner_setting` cho Tech và preload `native_language`. |
| **Language First Open** | `LanguageStartActivity` | `native_language`, `native_language_2`, `native_click`, `inter_splash`, `native_intro` | Hiển thị native đôi (`NATIVE_LARGE_ABOVE`) kèm `setupCtaZoomAnimation()`, đổi sang `native_click` khi chọn ngôn ngữ/dropdown, preload `native_intro`, và hiển thị `inter_splash` khi bấm Done. |
| **Language Setting** | `LanguageSettingActivity` | `banner_setting` | Load banner ở đáy màn hình qua `AdsHelper.loadBannerAds(...)`. |
| **Intro / Onboarding** | `TutorialActivity`, `IntroAdapter` | `native_intro`, `native_intro_2`, `native_intro_full`, `native_intro_full_2`, `native_intro_full1`, `native_intro_full1_2`, `inter_intro` | Hiển thị `native_intro` (`NATIVE_SMALL_ABOVE`), chèn trang quảng cáo full màn giữa slide 1-2 (`native_intro_full`) và slide 3-4 (`native_intro_full1`), preload `inter_intro` ở slide 2 và hiển thị sau bước Rate (lần mở 2, 5, 9) khi vào màn tiếp theo. |
| **Permission** | `PermissionActivity` | `native_per` | Hiển thị `native_per` (`NATIVE_LARGE_ABOVE`) kèm `setupCtaZoomAnimation()`. Tự động disable AppOpen khi mở hộp thoại cấp quyền hệ thống và enable lại ở `onResume()`. |
| **Welcome Back / Resume** | `WelcomeBackActivity`, `BaseActivity` | `resume_wb`, `native_wb` | Tự động bật AppOpen Resume trong `BaseActivity.onResume()` nếu `resume_wb` bật; preload và hiển thị resume ad tại `WelcomeBackActivity`. |
| **Setting** | `SettingActivity` | AppOpen Resume | Tự động bật lại AppOpen Resume sau 500ms trong `onResume()`. Không gắn `inter_all` tại màn Setting. |

---

## 3. Quy tắc Ánh xạ & Triển khai Quảng cáo qua `AdsHelper`

### 3.1. Banner & Collapse Banner Ads
- **Banner thường**:
  ```kotlin
  AdsHelper.loadBannerAds(
      activity = this,
      lifecycleOwner = this,
      frAds = binding.frBanner,
      adsKey = Constants.AdsKey.<ads_key>
  )
  ```
- **Collapse Banner**:
  ```kotlin
  AdsHelper.loadCollapseBanner(
      activity = this,
      lifecycleOwner = this,
      adsKey = Constants.AdsKey.<ads_key>,
      remoteKey = Constants.RemoteKeys.<remote_key>,
      frAds = binding.collapsibleBannerContainerView
  )
  ```

### 3.2. Native Ads (`AdsHelper.loadNative`)
Dựa vào mô tả trong cột **Placement** trên Google Sheet để chọn `NativeType` chính xác:

| Mô tả trong Google Sheet (`Placement`) | `NativeType` tương ứng | Layout sử dụng |
| :--- | :--- | :--- |
| *"Ads native to..., button ads đặt phía trên"* | `NativeType.NATIVE_LARGE_ABOVE` | `native_large_ads_with_button_above.xml` |
| *"Ads native to..., button ads đặt phía dưới"* | `NativeType.NATIVE_LARGE_BELOW` | `native_large_ads_with_button_bellow.xml` |
| *"Native nhỏ..., button ads đặt phía trên"* | `NativeType.NATIVE_SMALL_ABOVE` | `native_small_ads_with_button_above.xml` |
| *"Native nhỏ..., button ads đặt phía dưới"* | `NativeType.NATIVE_SMALL_BELOW` | `native_small_ads_with_button_bellow.xml` |
| *"Native dropdown to cuối màn..."* | `NativeType.NATIVE_DROPDOWN` | `native_large_ads_with_button_bellow_dropdown.xml` |
| *"Native siêu nhỏ / meta"* | `null` | `native_small_ads_meta.xml` |

- **Cách gọi chuẩn**:
  ```kotlin
  val nativeManager = AdsHelper.loadNative(
      activity = requireActivity(),
      lifecycleOwner = viewLifecycleOwner,
      frAds = binding.frAds,
      remoteKeyMain = Constants.RemoteKeys.<remote_key>,
      adsKeyMain = Constants.AdsKey.<ads_key>,
      type = NativeType.<TYPE>
  )
  binding.frAds.setupCtaZoomAnimation()
  ```

- **Quy tắc riêng cho `NativeType.NATIVE_DROPDOWN`** (ví dụ: `native_home_dropdown`, `native_play_dropdown`, `native_search_dropdown`, `native_continue_watching_dropdown`):
  1. Container `frAds` đặt ở cuối màn hình, đè lên trên phần nội dung (`FrameLayout` hoặc `ConstraintLayout` nằm trên `BottomNav`).
  2. Nếu Sheet quy định key thời gian reload riêng (ví dụ: `reload_interval_native_dropdown`, mặc định `60s`), truyền hoặc thiết lập `nativeManager.setIntervalReloadNative(intervalSeconds * 1000)` (nếu giá trị RemoteConfig `<= 0` thì fallback về mặc định `60_000L`).
  3. Khi người dùng click vào nút `icDropDown` (`R.id.icDropDown` bên trong layout `native_large_ads_with_button_bellow_dropdown.xml`), ẩn container quảng cáo (`binding.frAds.gone()`).
  4. Nếu kịch bản yêu cầu *"reload khi bấm các tab tại thanh nav"*, khi chuyển tab cần hiển thị lại `binding.frAds.visible()` và gọi reload/load lại Native Dropdown tương ứng của tab đó.

- **Quy tắc cho Native xen kẽ danh sách / ViewPager (`native_episode`, `native_home`)**:
  - Kiểm tra điều kiện `Admob.getInstance().checkCondition(context, Constants.RemoteKeys.<remote_key>)`.
  - Với kịch bản *"cách (n) tập phim đặt 1 native full"*:
    - Đọc `n` từ `RemoteConfigHelper.getInstance().get_config_long(context, Constants.RemoteKeys.<interval_key>)`.
    - Luôn có giá trị fallback mặc định theo Sheet (ví dụ: `val interval = configValue.takeIf { it > 0 } ?: 3L`) để tránh lỗi chia cho 0 khi RemoteConfig chưa trả về.

### 3.3. Interstitial Ads (`AdsHelper.loadAndShowInter`)
- **Nguyên tắc vàng**:
  - Mọi hành động chuyển màn hình hoặc thực thi tác vụ sau quảng cáo **PHẢI** đặt trong callback `onNextAction` của `AdsHelper.loadAndShowInter`.
  - **KHÔNG** gắn Interstitial khi người dùng bấm nút **Back** quay lại màn trước (trừ khi kịch bản ghi rõ).
  - **KHÔNG** gắn `inter_all` tại màn `SettingActivity` / `SettingsFragment`.

- **Trường hợp 1: Click chuyển màn trực tiếp** (`inter_home`, `inter_infor`, `inter_all`):
  ```kotlin
  AdsHelper.loadAndShowInter(
      activity = requireActivity(),
      remoteKey = Constants.RemoteKeys.<remote_key>,
      adsKey = Constants.AdsKey.<ads_key>
  ) {
      // Thực hiện chuyển màn hoặc mở nội dung tại đây
  }
  ```

### 3.4. Reward Ads (`loadReward`, `showReward`, `showDoubleReward`, `DialogReward`)
- **Bước 1 - Preload**: Luôn gọi `AdsHelper.loadReward(activity, Constants.AdsKey.<ads_key>, Constants.RemoteKeys.<remote_key>)` trong `initView()` của Activity/Fragment có chứa tính năng khóa hoặc nhận thưởng để quảng cáo sẵn sàng hiển thị.
- **Bước 2 - Hiển thị & Trao thưởng**:
  - **Mở khóa trực tiếp / Nhận thưởng đơn** (`reward_episode`, `reward_all`):
    ```kotlin
    AdsHelper.showReward(
        activity = requireActivity(),
        adsKey = Constants.AdsKey.<ads_key>,
        remoteKey = Constants.RemoteKeys.<remote_key>,
        isReloadAfterShow = true
    ) {
        // Mở khóa tập phim / nhận Daily Reward / khôi phục Check-in
    }
    ```
  - **Thông qua `DialogReward` (Xem 1 quảng cáo mở 1 item vs Xem 2 quảng cáo mở toàn bộ)**:
    - Hiển thị dialog: `DialogReward.newInstance(type = 1).safeShow(childFragmentManager)`
    - Lắng nghe kết quả qua `setFragmentResultListener(DialogReward.REQUEST_KEY, viewLifecycleOwner)`:
      - `DialogReward.ACTION_WATCH` -> Gọi `AdsHelper.showReward(...)` với key đơn (`rewarded_items`).
      - `DialogReward.ACTION_WATCH_ALL` -> Gọi `AdsHelper.showDoubleReward(...)` với key mở toàn bộ (`rewarded_all`).

### 3.5. App Open / Resume Ads (`enableAppOpen` & `dismissAppOpen`)
- Trước khi gọi bất kỳ Intent hệ thống nào làm ứng dụng tạm thời vào background (như Share app, mở trình duyệt xem Privacy Policy, chọn ảnh/file, xin quyền hệ thống), phải tắt tạm thời AppOpen Resume:
  ```kotlin
  AdsHelper.dismissAppOpen(requireActivity())
  ```
- Trong `onResume()` của Activity/Fragment đó, bật lại:
  ```kotlin
  AdsHelper.enableAppOpen(requireActivity(), viewLifecycleOwner)
  ```

---

## 4. Checklist Kiểm tra cho AI Agent khi Thực hiện Task Gắn Quảng Cáo

1. [ ] Đã tải và đọc toàn bộ các dòng trong Google Sheet được cung cấp chưa?
2. [ ] Đã lọc bỏ các dòng thuộc `ui.default_ui` (dấu `` ` ``) và các dòng bị đánh dấu hủy (dấu `x`) chưa?
3. [ ] Đã thêm đầy đủ `AdsKey` và `RemoteKeys` (kể cả các key interval/boolean phụ trong cột `Placement` & `Flow`) vào `Constants.kt` chưa?
4. [ ] Đã thêm các `RemoteKeys` quảng cáo mới vào `addListTurnOffRemoteKeys()` trong `SplashActivity.kt` chưa?
5. [ ] Các phương thức mới (như Native Dropdown có custom interval hay xử lý ẩn nút dropdown) đã được đóng gói gọn gàng trong `AdsHelper.kt` chưa?
6. [ ] Mọi luồng chuyển màn / mở khóa sau Inter hoặc Reward đã nằm trong callback `onNextAction` chưa?
