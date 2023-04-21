/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package androidx.media3.common;

import static java.lang.annotation.ElementType.TYPE_USE;

import android.os.Bundle;
import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import androidx.annotation.Nullable;
import androidx.media3.common.util.Assertions;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.common.util.Util;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Information about the playback device. */
public final class DeviceInfo implements Bundleable {

  /** Types of playback. One of {@link #PLAYBACK_TYPE_LOCAL} or {@link #PLAYBACK_TYPE_REMOTE}. */
  @Documented
  @Retention(RetentionPolicy.SOURCE)
  @Target(TYPE_USE)
  @IntDef({
    PLAYBACK_TYPE_LOCAL,
    PLAYBACK_TYPE_REMOTE,
  })
  public @interface PlaybackType {}
  /** Playback happens on the local device (e.g. phone). */
  public static final int PLAYBACK_TYPE_LOCAL = 0;
  /** Playback happens outside of the device (e.g. a cast device). */
  public static final int PLAYBACK_TYPE_REMOTE = 1;

  /** Unknown DeviceInfo. */
  public static final DeviceInfo UNKNOWN = new Builder(PLAYBACK_TYPE_LOCAL).build();

  /** Builder for {@link DeviceInfo}. */
  public static final class Builder {

    private final @PlaybackType int playbackType;

    private int minVolume;
    private int maxVolume;

    /**
     * Creates the builder.
     *
     * @param playbackType The {@link PlaybackType}.
     */
    public Builder(@PlaybackType int playbackType) {
      this.playbackType = playbackType;
    }

    /**
     * Sets the minimum supported device volume.
     *
     * <p>The minimum will be set to {@code 0} if not specified.
     *
     * @param minVolume The minimum device volume.
     * @return This builder.
     */
    @CanIgnoreReturnValue
    public Builder setMinVolume(@IntRange(from = 0) int minVolume) {
      this.minVolume = minVolume;
      return this;
    }

    /**
     * Sets the maximum supported device volume.
     *
     * @param maxVolume The maximum device volume, or {@code 0} to leave the maximum unspecified.
     * @return This builder.
     */
    @CanIgnoreReturnValue
    public Builder setMaxVolume(@IntRange(from = 0) int maxVolume) {
      this.maxVolume = maxVolume;
      return this;
    }

    /** Builds the {@link DeviceInfo}. */
    public DeviceInfo build() {
      Assertions.checkArgument(minVolume <= maxVolume);
      return new DeviceInfo(this);
    }
  }

  /** The type of playback. */
  public final @PlaybackType int playbackType;
  /** The minimum volume that the device supports. */
  @IntRange(from = 0)
  public final int minVolume;
  /** The maximum volume that the device supports, or {@code 0} if unspecified. */
  @IntRange(from = 0)
  public final int maxVolume;

  /**
   * @deprecated Use {@link Builder} instead.
   */
  @UnstableApi
  @Deprecated
  public DeviceInfo(
      @PlaybackType int playbackType,
      @IntRange(from = 0) int minVolume,
      @IntRange(from = 0) int maxVolume) {
    this(new Builder(playbackType).setMinVolume(minVolume).setMaxVolume(maxVolume));
  }

  private DeviceInfo(Builder builder) {
    this.playbackType = builder.playbackType;
    this.minVolume = builder.minVolume;
    this.maxVolume = builder.maxVolume;
  }

  @Override
  public boolean equals(@Nullable Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof DeviceInfo)) {
      return false;
    }
    DeviceInfo other = (DeviceInfo) obj;
    return playbackType == other.playbackType
        && minVolume == other.minVolume
        && maxVolume == other.maxVolume;
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + playbackType;
    result = 31 * result + minVolume;
    result = 31 * result + maxVolume;
    return result;
  }

  // Bundleable implementation.

  private static final String FIELD_PLAYBACK_TYPE = Util.intToStringMaxRadix(0);
  private static final String FIELD_MIN_VOLUME = Util.intToStringMaxRadix(1);
  private static final String FIELD_MAX_VOLUME = Util.intToStringMaxRadix(2);

  @UnstableApi
  @Override
  public Bundle toBundle() {
    Bundle bundle = new Bundle();
    if (playbackType != PLAYBACK_TYPE_LOCAL) {
      bundle.putInt(FIELD_PLAYBACK_TYPE, playbackType);
    }
    if (minVolume != 0) {
      bundle.putInt(FIELD_MIN_VOLUME, minVolume);
    }
    if (maxVolume != 0) {
      bundle.putInt(FIELD_MAX_VOLUME, maxVolume);
    }
    return bundle;
  }

  /** Object that can restore {@link DeviceInfo} from a {@link Bundle}. */
  @UnstableApi
  public static final Creator<DeviceInfo> CREATOR =
      bundle -> {
        int playbackType =
            bundle.getInt(FIELD_PLAYBACK_TYPE, /* defaultValue= */ PLAYBACK_TYPE_LOCAL);
        int minVolume = bundle.getInt(FIELD_MIN_VOLUME, /* defaultValue= */ 0);
        int maxVolume = bundle.getInt(FIELD_MAX_VOLUME, /* defaultValue= */ 0);
        return new DeviceInfo.Builder(playbackType)
            .setMinVolume(minVolume)
            .setMaxVolume(maxVolume)
            .build();
      };
}
