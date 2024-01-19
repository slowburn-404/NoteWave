package dev.borisochieng.notewave.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator
import dev.borisochieng.notewave.R
import dev.borisochieng.notewave.databinding.FragmentIntroSliderBinding
import dev.borisochieng.notewave.adapters.VpIntroSliderAdapter

class IntroSliderFragment : Fragment() {
    private var _binding: FragmentIntroSliderBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentIntroSliderBinding.inflate(inflater, container, false)

        navController = findNavController()

        val onBoardingScreens = arrayListOf(
            FirstIntroSliderScreenFragment(), SecondIntroSliderScreenFragment(), ThirdIntroSliderScreenFragment()
        )
        val adapter =  VpIntroSliderAdapter(onBoardingScreens, requireActivity().supportFragmentManager, lifecycle)
        val wormDotsIndicator: WormDotsIndicator = binding.introSliderWormDotsIndicator
        val onBoardingViewPager: ViewPager2 = binding.introSliderViewPager

        onBoardingViewPager.adapter = adapter
        wormDotsIndicator.attachTo(onBoardingViewPager)

        binding.iVNext.setOnClickListener {
            navController.navigate(R.id.action_introSliderFragment_to_getStartedFragment)
        }




        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}