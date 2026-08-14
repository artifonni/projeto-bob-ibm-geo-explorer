import { type HTMLAttributes, forwardRef } from 'react'
import { motion } from 'framer-motion'

interface CardProps extends HTMLAttributes<HTMLDivElement> {
  hoverable?: boolean
}

const Card = forwardRef<HTMLDivElement, CardProps>(
  ({ hoverable = true, children, className = '', ...props }, ref) => (
    <motion.div
      ref={ref}
      whileHover={hoverable ? { y: -4, boxShadow: '0 20px 40px rgba(16,185,129,0.08)' } : undefined}
      transition={{ type: 'spring', stiffness: 300, damping: 25 }}
      className={[
        'rounded-2xl border border-zinc-800 bg-zinc-900/80',
        'backdrop-blur-sm p-6',
        hoverable ? 'cursor-pointer' : '',
        className,
      ].join(' ')}
      {...(props as React.ComponentProps<typeof motion.div>)}
    >
      {children}
    </motion.div>
  )
)

Card.displayName = 'Card'
export default Card
