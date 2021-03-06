package com.raywenderlich.android.creatures.ui

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.raywenderlich.android.creatures.R
import com.raywenderlich.android.creatures.app.inflate
import com.raywenderlich.android.creatures.model.Creature
import android.support.v7.graphics.Palette
import android.view.animation.AnimationUtils
import com.raywenderlich.android.creatures.app.Constants
import kotlinx.android.synthetic.main.list_item_creature_card_jupiter.view.*


class CreatureCardAdapter(private val creatures: MutableList<Creature>) : RecyclerView.Adapter<CreatureCardAdapter.ViewHolder>() {

  var scrollDirection = ScrollDirection.DOWN
  var jupiterSpanSize = 2

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return when (viewType) {
      ViewType.OTHER.ordinal -> ViewHolder(parent.inflate(R.layout.list_item_creature_card))
      ViewType.JUPITER.ordinal -> ViewHolder(parent.inflate(R.layout.list_item_creature_card_jupiter))
      else -> throw IllegalArgumentException()
    }
  }

  override fun onBindViewHolder(holder: CreatureCardAdapter.ViewHolder, position: Int) {
    holder.bind(creatures[position])
  }

  override fun getItemCount() = creatures.size

  override fun getItemViewType(position: Int) : Int {
    val creature = creatures[position]
    return if (creature.planet == Constants.JUPITER) ViewType.JUPITER.ordinal else ViewType.OTHER.ordinal
  }

  fun spanSizeAtPosition(position: Int): Int {
    return if (creatures[position].planet == Constants.JUPITER) {
      jupiterSpanSize
    } else {
      1
    }
  }

  inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    private lateinit var creature: Creature

    init {
    	itemView.setOnClickListener(this)
    }

    fun bind(creature: Creature) {
      this.creature = creature
      val context = itemView.context
      val imageResource = context.resources.getIdentifier(creature.uri, null, context.packageName)
      itemView.creatureImage.setImageResource(imageResource)
      itemView.fullName.text = creature.fullName
      setBackgroundColors(context, imageResource)
      animateView(itemView)
    }

    override fun onClick(view: View) {
      val context = view.context
      val intent = CreatureActivity.newIntent(context, creature.id)
      context.startActivity(intent)
    }

    private fun setBackgroundColors(context: Context, imageResource: Int) {
      val image = BitmapFactory.decodeResource(context.resources, imageResource)
      Palette.from(image).generate { palette ->
        val backgroundColor = palette.getDominantColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
        itemView.creatureCard.setBackgroundColor(backgroundColor)
        itemView.nameHolder.setBackgroundColor(backgroundColor)
        val textColor = if (isColorDark(backgroundColor)) Color.WHITE else Color.BLACK
        itemView.fullName.setTextColor(textColor)
        if (itemView.slogan != null) {
          itemView.slogan.setTextColor(textColor)
        }
      }
    }

    private fun isColorDark(color: Int): Boolean {
      val darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
      return darkness >= 0.5
    }

    private fun animateView(viewToAnimate: View) {
      if (viewToAnimate.animation == null) {
        val animId = if(scrollDirection == ScrollDirection.DOWN) R.anim.slide_from_bottom else R.anim.slide_from_top
        val animation = AnimationUtils.loadAnimation(viewToAnimate.context, R.anim.scale_xy)
        viewToAnimate.animation = animation
      }
    }
  }

  enum class ScrollDirection {
    UP, DOWN
  }

  enum class ViewType {
    JUPITER, OTHER
  }
}