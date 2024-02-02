package dev.borisochieng.notewave.models

import android.os.Parcel
import android.os.Parcelable

data class NotesContentParcelable(
    val noteId: Long,
    val title: String,
    val content: String,
    val updateAt: String
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(noteId)
        parcel.writeString(title)
        parcel.writeString(content)
        parcel.writeString(updateAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NotesContentParcelable> {
        override fun createFromParcel(parcel: Parcel): NotesContentParcelable {
            return NotesContentParcelable(parcel)
        }

        override fun newArray(size: Int): Array<NotesContentParcelable?> {
            return arrayOfNulls(size)
        }
    }
}
