package com.blogappdemo.ui.main.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.blogappdemo.R
import com.blogappdemo.core.BaseViewHolder
import com.blogappdemo.data.model.Post
import com.blogappdemo.databinding.PostItemViewBinding
import com.blogappdemo.utils.TimeUtils
import com.blogappdemo.utils.hide
import com.blogappdemo.utils.show
import com.bumptech.glide.Glide

class HomeScreenAdapter(
    private val postList: List<Post>,
    onPostClickListener: OnPostClickListener,
) :
    RecyclerView.Adapter<BaseViewHolder<*>>() {

    //listeners likes/shared/comments/delete
    private var postClickListener: OnPostClickListener? = null

    init {
        postClickListener = onPostClickListener
    }

    //inflar el xml
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val itemBinding =
            PostItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeScreenViewHolder(itemBinding, parent.context)
    }

    //inflar a cada item con la data
    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when (holder) {
            is HomeScreenViewHolder -> holder.bind(postList[position])
        }
    }

    //tamaño de la list
    override fun getItemCount(): Int = postList.size

    //bindear la data
    private inner class HomeScreenViewHolder(
        val binding: PostItemViewBinding,
        val context: Context,
    ) : BaseViewHolder<Post>(binding.root) {
        override fun bind(item: Post) {
            //Header
            setupProfileInfo(item)
            addPostTimeStamp(item)
            setupPostDescription(item)
            //Post Image
            setupPostImage(item)
            //Like
            tintHeartIcon(item)
            setupLikeCount(item)
            setLikeClickAction(item)
            //Share
            setupShareCount(item)
            setShareClickAction(item)
            //Comment
            setupCommentCount(item)
            setCommentClickAction(item)
            //Delete
            setDeleteClickAction(item)
        }

        /* --------------------------------------- HEADER --------------------------------------- */
        //foto de perfil y nombre del usuario
        private fun setupProfileInfo(post: Post) {
            Glide.with(context).load(post.poster?.profile_picture).centerCrop()
                .into(binding.profilePicture)
            binding.tvProfileName.text = post.poster?.username
        }

        //timestamp
        private fun addPostTimeStamp(post: Post) {
            val createdAt = (post.created_at?.time?.div(1000L))?.let {
                TimeUtils.getTimeAgo(it.toInt())
            }
            binding.tvPostTimestamp.text = createdAt
        }

        //postDescription
        private fun setupPostDescription(post: Post) {
            if (post.post_description.isEmpty()) {
                binding.tvPostDescription.hide()
            } else {
                binding.tvPostDescription.text = post.post_description
            }
        }

    /* --------------------------------------- POST IMAGE --------------------------------------- */
        //imagen del post
        private fun setupPostImage(post: Post) {
            Glide.with(context).load(post.post_image).centerCrop().into(binding.ivPostImage)
        }

    /* --------------------------------------- LIKES --------------------------------------- */
        //pintar el like (favourite)
        private fun tintHeartIcon(post: Post) {
            if (!post.liked) {
                with(binding) {
                    btnLike.setIconTintResource(R.color.grey)
                    btnLike.setIconResource(R.drawable.ic_thumb_up_off_alt)
                    btnLike.setTextColor(ContextCompat.getColor(context, R.color.grey))
                }
            } else {
                with(binding) {
                    btnLike.setIconTintResource(R.color.blue_light)
                    btnLike.setIconResource(R.drawable.ic_thumb_up)
                    btnLike.setTextColor(ContextCompat.getColor(context, R.color.blue_light))
                }
            }
        }

        //contador de likes
        @SuppressLint("SetTextI18n")
        private fun setupLikeCount(post: Post) {
            if (post.likes > 0) {
                with(binding) {
                    tvLikeCount.show()
                    tvLikeCount.text = "${post.likes}"
                }
            } else {
                binding.tvLikeCount.hide()
            }
        }

        //accion al click del like
        private fun setLikeClickAction(post: Post) {
            binding.btnLike.setOnClickListener {
                if (post.liked) post.apply { liked = false } else post.apply { liked = true }
                //pintar color segun estado
                tintHeartIcon(post)
                //enviar al fragment si fue linkeado y su estado en ese momento
                postClickListener?.onLikeButtonClick(post, post.liked)
            }
        }

    /* --------------------------------------- SHARED --------------------------------------- */
        //contador de compartir
        @SuppressLint("SetTextI18n")
        private fun setupShareCount(post: Post) {
            if (post.shares > 0) {
                if (post.shares == 1L) {
                    with(binding) {
                        tvShareCount.show()
                        tvShareCount.text = "${post.shares} vez compartido"
                    }
                } else {
                    with(binding) {
                        tvShareCount.show()
                        tvShareCount.text = "${post.shares} veces compartido"
                    }
                }
            } else {
                binding.tvShareCount.hide()
            }
        }

        //al hacer click en compartir
        private fun setShareClickAction(post: Post) {
            binding.btnShare.setOnClickListener {
                if (post.shared) post.apply { shared = false } else post.apply { shared = true }
                sharePost(post)
                //enviar al fragment si fue compartido y su estado en ese momento
                postClickListener?.onShareButtonClick(post, post.shared)
            }
        }

        //intent para compartir post
        private fun sharePost(post: Post) {
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, post.post_image )
                type = "text/*"
            }
            val shareIntent = Intent.createChooser(intent, null)
            startActivity(context, shareIntent, null)
        }

    /* --------------------------------------- COMMENTS --------------------------------------- */
        //contador de comment
        @SuppressLint("SetTextI18n")
        private fun setupCommentCount(post: Post) {
            if (post.comments > 0) {
                if (post.comments == 1L) {
                    with(binding) {
                        tvCommentCount.show()
                        tvCommentCount.text = "${post.comments} comentario"
                    }
                } else {
                    with(binding) {
                        tvCommentCount.show()
                        tvCommentCount.text = "${post.comments} comentarios"
                    }
                }

            } else {
                binding.tvCommentCount.hide()
            }
        }

        //accion al click del comment
        private fun setCommentClickAction(post: Post) {
            binding.btnComment.setOnClickListener {
                if (post.commented) post.apply { commented = false } else post.apply { commented = true }
                //pintar color segun estado
                tintHeartIcon(post)
                //enviar al fragment si fue linkeado y su estado en ese momento
                postClickListener?.onCommentButtonClick(post, post.commented)
            }
        }

    /* --------------------------------------- DELETE --------------------------------------- */
        private fun setDeleteClickAction(post: Post) {
            binding.btnDelete.setOnClickListener {
                postClickListener?.onDeleteButtonClick(post)
            }
        }
    }
}